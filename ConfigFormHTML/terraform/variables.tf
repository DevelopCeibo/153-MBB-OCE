variable "region" {
  description = "region"
  type = string
}

variable "table_name"{
  description = "Nombre de la tabla en DynamoDB"
  type = string
}

variable "eloqua_api_name" {
  description = "Nombre de rest api de la integración con Eloqua"
  type = string
}