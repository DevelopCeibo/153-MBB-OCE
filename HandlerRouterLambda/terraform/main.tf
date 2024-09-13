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

########## Creando archivo .JAR ##########
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

########## Creando Bucket en S3 ##########
resource "aws_s3_bucket" "lambda_handler_router_bucket" {
  bucket = "lambda-handler-router-bucket"
}

resource "aws_s3_object" "lambda_handler_router_bucket_jar" {
  bucket = aws_s3_bucket.lambda_handler_router_bucket.id
  key    = "lambda/configure-entity-lambda.jar"
  source = "${path.module}/../target/HandlerRouterLambda-1.0-SNAPSHOT.jar"
  server_side_encryption = "AES256"
  depends_on = [null_resource.build_jar]
}


########## Creando SQS ##########
resource "aws_sqs_queue" "notify_queue" {
  name                      = "sqs_handler_router_notify"
  delay_seconds             = 0
  max_message_size          = 262144
  message_retention_seconds = 345600
  receive_wait_time_seconds = 0
  visibility_timeout_seconds = 30
  tags = {
    Environment = "dev"
  }
}

######## Creando grupo de Logs de para la lambda  ########
resource "aws_cloudwatch_log_group" "handle_router_lambda_log_group" {
  name              = "/aws/lambda/handle-router-lambda"
  retention_in_days = 5

}

######## Definiendo el rol de la Lambda ########
resource "aws_iam_role" "lambda_handler_router_role" {
  name = "lambda_handler_router_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action    = "sts:AssumeRole",
      Effect    = "Allow",
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

######## Políticas de CloudWatch Logs para la Lambda ########
resource "aws_iam_policy" "lambda_logging_policy" {
  name        = "lambda_logging_policy"
  description = "Permite a Lambda crear y escribir logs en CloudWatch"
  policy      = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = "logs:CreateLogGroup",
        Resource = "arn:aws:logs:${var.region}:${data.aws_caller_identity.current.account_id}:log-group:/aws/lambda/handle-router-lambda:*"
      },
      {
        Effect   = "Allow",
        Action   = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = "arn:aws:logs:${var.region}:${data.aws_caller_identity.current.account_id}:log-group:/aws/lambda/handle-router-lambda:*"
      }
    ]
  })
}

######## Políticas de SQS para la Lambda ########
resource "aws_iam_policy" "lambda_sqs_policy" {
  name        = "lambda_sqs_policy"
  description = "Permite a Lambda interactuar con SQS"
  policy      = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = "sqs:SendMessage",
        Resource = aws_sqs_queue.notify_queue.arn
      }
    ]
  })
}

######## Adjuntar las políticas al rol de la Lambda ########
resource "aws_iam_role_policy_attachment" "lambda_sqs_policy_attachment" {
  role       = aws_iam_role.lambda_handler_router_role.name
  policy_arn = aws_iam_policy.lambda_sqs_policy.arn
}

resource "aws_iam_role_policy_attachment" "lambda_logging_policy_attachment" {
  role       = aws_iam_role.lambda_handler_router_role.name
  policy_arn = aws_iam_policy.lambda_logging_policy.arn
}

######## Creando Lambda Function ########
resource "aws_lambda_function" "handler_router_lambda" {
  function_name = "handler_router_lambda"
  s3_bucket     = aws_s3_bucket.lambda_handler_router_bucket.bucket
  s3_key        = aws_s3_object.lambda_handler_router_bucket_jar.key
  handler       = "com.telecom.handler.RouterHandler::handleRequest"
  runtime       = "java17"
  role          = aws_iam_role.lambda_handler_router_role.arn
  memory_size   = 512
  timeout       = 15
  environment {
    variables = {
      QUEUE_URL = aws_sqs_queue.notify_queue.id
    }
  }
}

data "aws_api_gateway_rest_api" "eloqua_api" {
  name        = "eloqua-rest-api"
}

resource "aws_api_gateway_resource" "notify_route" {
  rest_api_id = data.aws_api_gateway_rest_api.eloqua_api.id
  parent_id   = data.aws_api_gateway_rest_api.eloqua_api.root_resource_id
  path_part   = "notify"
}

resource "aws_api_gateway_method" "post_method" {
  rest_api_id   = data.aws_api_gateway_rest_api.eloqua_api.id
  resource_id   = aws_api_gateway_resource.notify_route.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda_integration" {
  rest_api_id             = data.aws_api_gateway_rest_api.eloqua_api.id
  resource_id             = aws_api_gateway_resource.notify_route.id
  http_method             = aws_api_gateway_method.post_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.handler_router_lambda.invoke_arn
}

resource "aws_api_gateway_deployment" "lambda_deployment" {
  depends_on = [aws_api_gateway_integration.lambda_integration]
  rest_api_id = data.aws_api_gateway_rest_api.eloqua_api.id
  stage_name  = "dev"
}

resource "aws_lambda_permission" "lambda_api_permission" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.handler_router_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${data.aws_api_gateway_rest_api.eloqua_api.execution_arn}/*/*"
}
