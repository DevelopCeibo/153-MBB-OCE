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

##### bucket s3 #######
resource "aws_s3_bucket" "config_form_html_bucket" {
  bucket = "config-form-html-bucket"
}

resource "aws_s3_bucket_public_access_block" "config_form_html_bucket_public_access" {
  bucket = aws_s3_bucket.config_form_html_bucket.id

  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = true
  restrict_public_buckets = true
}


resource "aws_s3_bucket_website_configuration" "website-configuration" {
  bucket = aws_s3_bucket.config_form_html_bucket.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

resource "aws_s3_object" "config_form" {
  bucket = aws_s3_bucket.config_form_html_bucket.bucket
  key    = "index.html"
  source = "${path.module}/../src/index.html"
  server_side_encryption = "AES256"
  tags = {
    Name        = "config_form_bucket"
    Environment = "Dev"
  }
  content_type = "text-html"
  cache_control = "no-cache"

  # Detectar cambios en el archivo utilizando su hash
  etag = filesha256("${path.module}/../src/index.html")
}

# Crear un Origin Access Identity (OAI) para que CloudFront tenga acceso privado al bucket de S3
resource "aws_cloudfront_origin_access_identity" "oai" {
  comment = "OAI for S3 bucket config-form-html-bucket"
}

# Añadir una política de acceso para que solo CloudFront pueda acceder al bucket
resource "aws_s3_bucket_policy" "config_form_html_bucket_policy" {
  bucket = aws_s3_bucket.config_form_html_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = {
          AWS = "${aws_cloudfront_origin_access_identity.oai.iam_arn}"
        }
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.config_form_html_bucket.arn}/*"
      }
    ]
  })
}

##### Cloudfront ########

# Crear la distribución de CloudFront
resource "aws_cloudfront_distribution" "cloudfront_distribution" {
  origin {
    domain_name = aws_s3_bucket.config_form_html_bucket.bucket_regional_domain_name
    origin_id   = "S3-config-form-html-bucket"

    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.oai.cloudfront_access_identity_path
    }
  }

  enabled             = true
#   is_ipv6_enabled     = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]

    target_origin_id = "S3-config-form-html-bucket"

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  price_class = "PriceClass_100"

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Name        = "config_form_cloudfront_distribution"
    Environment = "Dev"
  }
}

##########  API Gateway #######################

data "aws_api_gateway_rest_api" "my_eloqua_api" {
  name = var.eloqua_api_name
}

resource "aws_api_gateway_resource" "path_configure" {
  rest_api_id = data.aws_api_gateway_rest_api.my_eloqua_api.id
  parent_id   = data.aws_api_gateway_rest_api.my_eloqua_api.root_resource_id
  path_part   = "configure"
}

resource "aws_api_gateway_method" "get_method" {
  rest_api_id   = data.aws_api_gateway_rest_api.my_eloqua_api.id
  resource_id   = aws_api_gateway_resource.path_configure.id
  http_method   = "GET"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "apigateway_clodfront_integration" {
  rest_api_id             = data.aws_api_gateway_rest_api.my_eloqua_api.id
  resource_id             = aws_api_gateway_resource.path_configure.id
  http_method             = aws_api_gateway_method.get_method.http_method
  integration_http_method = "GET"
  type                    = "HTTP_PROXY"
  uri                     = "https://${aws_cloudfront_distribution.cloudfront_distribution.domain_name}/index.html"
}

resource "aws_api_gateway_deployment" "cloudfront_deployment" {
  rest_api_id = data.aws_api_gateway_rest_api.my_eloqua_api.id
  stage_name  = "dev"
}