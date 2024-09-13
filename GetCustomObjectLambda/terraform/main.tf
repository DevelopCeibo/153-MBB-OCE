terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.6"
    }
  }
}

provider "aws" {
  region = var.region
}

data "aws_caller_identity" "current" {}

resource "null_resource" "build_jar" {
  provisioner "local-exec" {
    command = "mvn clean package"
    working_dir = "${path.module}/../"
  }

  triggers = {
    always_run = "${timestamp()}"
  }
}

resource "aws_s3_bucket" "lambda_get_custom_objet" {
  bucket = "lambda-get-custom-objects-bucket"
}

resource "aws_s3_object" "lambda_get_custom_object_jar" {
  bucket = aws_s3_bucket.lambda_get_custom_objet.id
  key    = "lambda/get-custom-object-lambda.jar"
  source = "${path.module}/../target/GetCustomObjectLambda-1.0-SNAPSHOT.jar"
  server_side_encryption = "AES256"
  depends_on = [null_resource.build_jar]
}

data "aws_iam_role" "lambda_exec_role" {
  name = "lambda-exec-role"
}

resource "aws_iam_role_policy_attachment" "lambda_exec_role_attach" {
  role       = data.aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_lambda_function" "get_custom_object_lambda" {
  function_name = "get-custom-object-lambda"
  s3_bucket     = aws_s3_bucket.lambda_get_custom_objet.bucket
  s3_key        = aws_s3_object.lambda_get_custom_object_jar.key
  handler       = "com.telecom.handler.GetCustomObjectHandler::handleRequest"
  runtime       = "java17"
  role          = data.aws_iam_role.lambda_exec_role.arn
  memory_size   = 512
  timeout       = 15
  environment {
    variables = {
      eloquaToken = var.eloqua_token
    }
  }
}

data "aws_api_gateway_rest_api" "eloqua_api" {
  name        = var.eloqua_api_name
}

resource "aws_api_gateway_resource" "get_custom_object_path" {
  rest_api_id = data.aws_api_gateway_rest_api.eloqua_api.id
  parent_id   = data.aws_api_gateway_rest_api.eloqua_api.root_resource_id
  path_part   = "custom_object_fields"
}

resource "aws_api_gateway_method" "get_method_custom_object" {
  rest_api_id   = data.aws_api_gateway_rest_api.eloqua_api.id
  resource_id   = aws_api_gateway_resource.get_custom_object_path.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda_integration" {
  rest_api_id             = data.aws_api_gateway_rest_api.eloqua_api.id
  resource_id             = aws_api_gateway_resource.get_custom_object_path.id
  http_method             = aws_api_gateway_method.get_method_custom_object.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.get_custom_object_lambda.invoke_arn

  request_templates = {
    "application/json" = <<EOF
    {
      "queryStringParameters": {
        #foreach($key in $input.params().querystring.keySet())
        "$key": "$input.params().querystring.get($key)",
        #end
      }
    }
    EOF
  }
}


resource "aws_api_gateway_deployment" "lambda_deployment" {
  depends_on = [aws_api_gateway_integration.lambda_integration]
  rest_api_id = data.aws_api_gateway_rest_api.eloqua_api.id
  stage_name  = "dev"
}


resource "aws_lambda_permission" "lambda_api_permission" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.get_custom_object_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${data.aws_api_gateway_rest_api.eloqua_api.execution_arn}/*/*"
}