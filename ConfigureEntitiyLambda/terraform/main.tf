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

resource "aws_s3_bucket" "lambda_configure_entity_bucket" {
  bucket = "lambda-configure-entity-bucket"
}

resource "aws_s3_object" "lambda_configure_jar" {
  bucket = aws_s3_bucket.lambda_configure_entity_bucket.id
  key    = "lambda/configure-entity-lambda.jar"
  source = "${path.module}/../target/ConfigureEntityLambda-1.0-SNAPSHOT.jar"
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

resource "aws_iam_policy" "lambda_dynamodb_policy" {
  name        = "lambda-dynamodb-policy-configure"
  description = "Permite a Lambda realizar PutItem en DynamoDB"
  policy      = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "dynamodb:GetItem",
          "dynamodb:UpdateItem"
        ],
        Resource = "arn:aws:dynamodb:${var.region}:${data.aws_caller_identity.current.account_id}:table/${var.table_name}"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_dynamodb_policy_attachment" {
  role       = data.aws_iam_role.lambda_exec_role.name
  policy_arn = aws_iam_policy.lambda_dynamodb_policy.arn
}

resource "aws_lambda_function" "configure_entity_lambda" {
  function_name = "configure-entity-lambda"
  s3_bucket     = aws_s3_bucket.lambda_configure_entity_bucket.bucket
  s3_key        = aws_s3_object.lambda_configure_jar.key
  handler       = "com.telecom.handler.ConfigureEntityHandler::handleRequest"
  runtime       = "java17"
  role          = data.aws_iam_role.lambda_exec_role.arn
  memory_size   = 512
  timeout       = 15
  environment {
    variables = {
      ELOQUA_AUTH_TOKEN = var.eloqua_token
    }
  }
}

data "aws_api_gateway_rest_api" "eloqua_api" {
  name        = var.eloqua_api_name
}

data "aws_api_gateway_resource" "path_configure" {
  rest_api_id = data.aws_api_gateway_rest_api.eloqua_api.id
  path   = "/configure"
}

resource "aws_api_gateway_method" "post_method" {
  rest_api_id   = data.aws_api_gateway_rest_api.eloqua_api.id
  resource_id   = data.aws_api_gateway_resource.path_configure.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda_integration" {
  rest_api_id             = data.aws_api_gateway_rest_api.eloqua_api.id
  resource_id             = data.aws_api_gateway_resource.path_configure.id
  http_method             = aws_api_gateway_method.post_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.configure_entity_lambda.invoke_arn
}

resource "aws_api_gateway_deployment" "lambda_deployment" {
  depends_on = [aws_api_gateway_integration.lambda_integration]
  rest_api_id = data.aws_api_gateway_rest_api.eloqua_api.id
  stage_name  = "dev"
}

resource "aws_lambda_permission" "lambda_api_permission" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.configure_entity_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${data.aws_api_gateway_rest_api.eloqua_api.execution_arn}/*/*"
}
