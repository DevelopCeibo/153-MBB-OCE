variable "region" {
  description = "region"
  type = string
}

variable "table_name"{
  description = "Nombre de la tabla en DynamoDB"
  type = string
}

variable "aws_sqs_queue_name" {
  description = "Nombre del SQS existente"
  type = string
}

variable "eloqua_token" {
  description = "Token de acceso a Eloqua"
  sensitive = true
  type = string
}