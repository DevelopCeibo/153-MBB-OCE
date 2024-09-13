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
resource "aws_s3_bucket" "lambda_notify_entity_bucket" {
  bucket = "lambda-notify-entity-bucket"
}

resource "aws_s3_object" "lambda_notify_entity_bucket_jar" {
  bucket = aws_s3_bucket.lambda_notify_entity_bucket.id
  key    = "lambda/notify-entity-lambda.jar"
  source = "${path.module}/../target/NotificationEntityLambda-1.0-SNAPSHOT.jar"
  server_side_encryption = "AES256"
  depends_on = [null_resource.build_jar]
}


########## trayenda data del  SQS ##########
data "aws_sqs_queue" "notify_queue" {
  name =  var.aws_sqs_queue_name
}

######## Creando grupo de Logs de para la lambda  ########
resource "aws_cloudwatch_log_group" "notify_entity_lambda_log_group" {
  name              = "/aws/lambda/notify-entity-lambda"
  retention_in_days = 0

}

######## Definiendo el rol de la Lambda ########
resource "aws_iam_role" "lambda_notify_entity_role" {
  name = "lambda_notify_entity_role"
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
  name        = "lambda_notify_logging_policy"
  description = "Permite a Lambda crear y escribir logs en CloudWatch"
  policy      = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = "logs:CreateLogGroup",
        Resource = "arn:aws:logs:${var.region}:${data.aws_caller_identity.current.account_id}:*"
      },
      {
        Effect   = "Allow",
        Action   = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = "arn:aws:logs:${var.region}:${data.aws_caller_identity.current.account_id}:log-group:/aws/lambda/notificationEntities:*"
      }
    ]
  })
}

######## Políticas de SQS para la Lambda ########
# Permiso para interactuar con SQS
resource "aws_iam_policy" "lambda_sqs_policy" {
  name        = "lambda_notify_sqs_policy"
  description = "Permite a Lambda recibir y eliminar mensajes de SQS"
  policy      = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ],
        Resource = data.aws_sqs_queue.notify_queue.arn
      }
    ]
  })
}

# Permiso para obtener datos de DynamoDB
resource "aws_iam_policy" "lambda_dynamodb_policy" {
  name        = "lambda_notify_dynamodb_policy"
  description = "Permite a Lambda obtener elementos de DynamoDB"
  policy      = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = "dynamodb:GetItem",
        Resource = "arn:aws:dynamodb:${var.region}:${data.aws_caller_identity.current.account_id}:table/${var.table_name}"
      }
    ]
  })
}


######## Adjuntar las políticas al rol de la Lambda ########
resource "aws_iam_role_policy_attachment" "lambda_sqs_policy_attachment" {
  role       = aws_iam_role.lambda_notify_entity_role.name
  policy_arn = aws_iam_policy.lambda_sqs_policy.arn
}

resource "aws_iam_role_policy_attachment" "lambda_logging_policy_attachment" {
  role       = aws_iam_role.lambda_notify_entity_role.name
  policy_arn = aws_iam_policy.lambda_logging_policy.arn
}

resource "aws_iam_role_policy_attachment" "lambda_dynamodb_policy_attachment" {
  role       = aws_iam_role.lambda_notify_entity_role.name
  policy_arn = aws_iam_policy.lambda_dynamodb_policy.arn
}

######## Creando Lambda Function ########
resource "aws_lambda_function" "notify_entity_lambda" {
  function_name = "notify_entity_lambda"
  s3_bucket     = aws_s3_bucket.lambda_notify_entity_bucket.bucket
  s3_key        = aws_s3_object.lambda_notify_entity_bucket_jar.key
  handler       = "com.telecom.handler.NotificationEntityHandler::handleRequest"
  runtime       = "java17"
  role          = aws_iam_role.lambda_notify_entity_role.arn
  memory_size   = 512
  timeout       = 15
  environment {
    variables = {
      ELOQUA_AUTH_TOKEN = var.eloqua_token
    }
  }
}

#Enlace SQS con la Lambda para que reciba eventos
resource "aws_lambda_event_source_mapping" "lambda_sqs_event_mapping" {
  event_source_arn = data.aws_sqs_queue.notify_queue.arn
  function_name    = aws_lambda_function.notify_entity_lambda.arn
  enabled          = true
  batch_size       = 1  # Número de mensajes que Lambda procesa por invocación
}
