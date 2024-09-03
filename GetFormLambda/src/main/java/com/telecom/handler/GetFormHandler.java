package com.telecom.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;

public class GetFormHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        // Obtener el logger del contexto
        LambdaLogger logger = context.getLogger();

        // Obtener los parámetros de la query
        Map<String, String> queryParams = apiGatewayProxyRequestEvent.getQueryStringParameters();

        // Obtener los parámetros con valores por defecto
        String instance_id = queryParams != null ? queryParams.getOrDefault("instance_id", "default") : "default";
        String install_id = queryParams != null ? queryParams.getOrDefault("install_id", "default") : "default";
        String user_name = queryParams != null ? queryParams.getOrDefault("user_name", "default") : "default";
        String user_id = queryParams != null ? queryParams.getOrDefault("user_id", "default") : "default";
        String site_name = queryParams != null ? queryParams.getOrDefault("site_name", "default") : "default";
        String site_id = queryParams != null ? queryParams.getOrDefault("site_id", "default") : "default";
        String app_id = queryParams != null ? queryParams.getOrDefault("app_id", "default") : "default";

        // Loguear los parámetros
        logger.log("instance_id: " + instance_id);
        logger.log("install_id: " + install_id);
        logger.log("user_name: " + user_name);
        logger.log("user_id: " + user_id);
        logger.log("site_name: " + site_name);
        logger.log("site_id: " + site_id);
        logger.log("app_id: " + app_id);

        // Construir el HTML
        String htmlResponse = """
                <!DOCTYPE html>
                <html lang="es">
                                
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Selector de Custom Object Fields y Editor de Template HTML</title>
                  <style>
                    html {
                      font-size: small;
                      background-color: #FEFEFE;
                    }
                                
                    body {
                      font-family: Arial, sans-serif;
                      max-width: 800px;
                      margin: 0 auto;
                      padding: 20px;
                    }
                                
                    #customObjectForm,
                    #fieldsContainer,
                    #templateContainer {
                      margin-bottom: 20px;
                    }
                                
                                
                    #invoicesBaseUrlForm>input {
                      width: 83%;
                    }
                                
                    #customObjectForm>input {
                      width: 43%;
                    }
                                
                    #customObjectForm>button {
                      width: 40%;
                    }
                                
                    input,
                    button {
                      margin: 10px 0;
                      padding: 5px;
                    }
                                
                    #fieldsContainer {
                      display: flex;
                      flex-wrap: wrap;
                      gap: 30px;
                    }
                                
                    #pivotFieldSection {
                      display: flex;
                      align-items: center;
                      gap: 10px;
                    }
                                
                    #fieldsContainer div {
                      margin-bottom: 10px;
                    }
                                
                    #selectedFields {
                      margin-top: 20px;
                    }
                                
                    #htmlEditor {
                      border: 1px solid #ccc;
                      min-height: 200px;
                      padding: 10px;
                      font-family: monospace;
                      white-space: pre-wrap;
                      overflow-wrap: break-word;
                    }
                                
                    .tag {
                      color: #0000FF;
                    }
                                
                    .attribute {
                      color: #FF0000;
                    }
                                
                    .value {
                      color: #008000;
                    }
                                
                    /* Ocultar el contenido resaltado detrás del editor */
                    #htmlHighlight {
                      position: absolute;
                      top: 0;
                      left: 0;
                      pointer-events: none;
                      opacity: 0;
                    }
                                
                    #htmlContainer {
                      position: relative;
                    }
                  </style>
                </head>
                                
                <body>
                  <h1>Selector de Custom Object Fields y Editor de Template HTML</h1>
                                
                  <!-- Campo de entrada para invoicesBaseUrl -->
                  <div id="invoicesBaseUrlForm">
                    <label for="invoicesBaseUrl">Invoice Base URL:</label>
                    <input type="text" id="invoicesBaseUrl" placeholder="Ingrese el invoicesBaseUrl">
                  </div>
                  <!-- Campo de entrada para selección del pivotField -->
                  <div id="pivotFieldSection">
                    <label>Seleccione el campo de pivote:</label><br>
                    <input type="radio" id="contactId" name="pivotField" value="contactId">
                    <label for="contactId">ID de Contacto</label><br>
                    <input type="radio" id="clientNumber" name="pivotField" value="name">
                    <label for="clientNumber">Nro de Cliente</label>
                  </div>
                  <!-- Campo de entrada para customObjectId -->
                  <div id="customObjectForm">
                    <label for="customObjectId">Custom Object ID:</label>
                    <input type="text" id="customObjectId" placeholder="Ingrese el ID del Custom Object">
                    <button onclick="fetchCustomObjectFields()">Obtener campos</button>
                  </div>
                  <div id="fieldsSection"></div>
                  <div id="selectedFields"></div>
                  <div id="templateContainer">
                    <h2>Editor de Template HTML:</h2>
                    <div id="htmlContainer">
                      <pre id="htmlHighlight"></pre>
                      <div id="htmlEditor" contenteditable="true"></div>
                    </div>
                    <button onclick="updateTemplate()">Actualizar Template</button>
                  </div>
                                
                  <script>
                    let fieldsData = [];
                    let isHighlighting = false;
                                
                    async function fetchCustomObjectFields() {
                      const customObjectId = document.getElementById('customObjectId').value;
                      const invoicesBaseUrl = document.getElementById('invoicesBaseUrl').value;
                      if (!customObjectId) {
                        alert('Por favor, ingrese un ID de Custom Object');
                        return;
                      }
                                
                      if (!invoicesBaseUrl) {
                        alert('Por favor, ingrese el invoicesBaseUrl');
                        return;
                      }
                                
                      try {
                        const response = await fetch(`https://p3tg75utdi.execute-api.us-east-1.amazonaws.com/custom_object_fields?id=${customObjectId}`);
                        const data = await response.json();
                        fieldsData = data.fields;
                        displayFields(fieldsData);
                      } catch (error) {
                        console.error('Error:', error);
                        alert('Hubo un error al obtener los campos. Por favor, intente nuevamente.');
                      }
                    }
                                
                    function displayFields(fields) {
                      const fieldsSection = document.getElementById('fieldsSection');
                      fieldsSection.innerHTML = '<h2>Campos disponibles:</h2>';
                                
                      const fieldsContainer = document.createElement('div');
                      fieldsContainer.id = 'fieldsContainer';
                      fieldsSection.appendChild(fieldsContainer);
                                
                                
                      fields.forEach(field => {
                        const fieldDiv = document.createElement('div');
                        fieldDiv.innerHTML = `
                                    <input type="checkbox" id="${field.id}" name="field" value="${field.id}">
                                    <label for="${field.id}">${field.name} (${field.internalName})</label>
                                `;
                        fieldsContainer.appendChild(fieldDiv);
                      });
                                
                      const selectButton = document.createElement('button');
                      selectButton.textContent = 'Seleccionar campos';
                      selectButton.onclick = showSelectedFields;
                      fieldsSection.appendChild(selectButton);
                    }
                                
                    function showSelectedFields() {
                      const selectedFields = document.querySelectorAll('input[name="field"]:checked');
                      generateTemplate(selectedFields);
                    }
                                
                    function generateTemplate(selectedFields) {
                      let template = "<tr>\\n";
                      selectedFields.forEach((field, index) => {
                        const fieldData = fieldsData.find(f => f.id === field.value);
                        template += `  <td>{{${fieldData.name}}}</td>\\n`;
                      });
                      template += "</tr>";
                                
                      const htmlEditor = document.getElementById('htmlEditor');
                      htmlEditor.textContent = template;
                      highlightSyntax();
                    }
                                
                    function highlightSyntax() {
                      if (isHighlighting) return;
                      isHighlighting = true;
                                
                      const htmlEditor = document.getElementById('htmlEditor');
                      const content = htmlEditor.innerText;
                                
                      const highlighted = content.replace(/(&lt;|<)(\\/?[a-z]+)(&gt;|>)|{{([^}]*)}}/gi, (match, lt, tag, gt, field) => {
                        if (tag) {
                          return `<span class="tag">${lt}${tag}${gt}</span>`;
                        } else if (field) {
                          return `{{<span class="value">${field}</span>}}`;
                        }
                        return match;
                      });
                                
                      const htmlHighlight = document.getElementById('htmlHighlight');
                      htmlHighlight.innerHTML = highlighted;
                                
                      isHighlighting = false;
                    }
                                
                    async function updateTemplate() {
                      const customObjectId = document.getElementById('customObjectId').value;
                      const invoicesBaseUrl = document.getElementById('invoicesBaseUrl').value;
                      const htmlEditor = document.getElementById('htmlEditor');
                      let template = htmlEditor.innerText;
                                
                      if (!customObjectId) {
                        alert('Por favor, ingrese un ID de Custom Object');
                        return;
                      }
                                
                      if (!invoicesBaseUrl) {
                        alert('Por favor, ingrese el invoicesBaseUrl');
                        return;
                      }
                                
                      // Obtener el valor del pivotField
                      const pivotFieldElement = document.querySelector('input[name="pivotField"]:checked');
                      if (!pivotFieldElement) {
                        alert('Por favor, seleccione un campo de pivote');
                        return;
                      }
                      const pivotField = pivotFieldElement.value;
                                
                      const selectedFields = document.querySelectorAll('input[name="field"]:checked');
                      const originalCustomObjectFields = Array.from(selectedFields).map(field => {
                        const fieldData = fieldsData.find(f => f.id === field.value);
                        return {
                          id: field.value,
                          name: fieldData.name
                        };
                      });
                                
                      // Extraer campos del template
                      const fieldsFromTemplate = extractFieldsFromTemplate(template);
                                
                      // Reconstruir customObjectFields
                      const updatedCustomObjectFields = rebuildCustomObjectFields(fieldsFromTemplate, originalCustomObjectFields);
                                
                      // Limpiar el template eliminando espacios innecesarios y saltos de línea
                      template = template.replace(/\\s+/g, ' ').trim();
                      template = template.replace(/{{.*?}}/g, "%s");  // Remueve saltos de línea y espacios extra
                                
                      // Agregar pivotField a jsonData
                      const jsonData = JSON.stringify({
                        customObjectId: customObjectId,
                        customObjectFields: JSON.stringify(updatedCustomObjectFields),
                        template: template,
                        invoicesBaseUrl: invoicesBaseUrl,
                        pivotField: pivotField  // Aquí agregamos el nuevo campo
                      });
                                
                      const params = new URLSearchParams(window.location.search);
                                
                      const instance_id = params.get('instance_id') || "";
                      const install_id = params.get('install_id') || "";
                      const user_name = params.get('user_name') || "";
                      const user_id = params.get('user_id') || "";
                      const site_name = params.get('site_name') || "";
                      const site_id = params.get('site_id') || "";
                      const app_id = params.get('app_id') || "";
                      const asset_id = params.get('asset_id') || "";
                      const asset_type = params.get('asset_type') || "";
                      const asset_name = params.get('asset_name') || "";
                                
                      const urlFetch = "https://p3tg75utdi.execute-api.us-east-1.amazonaws.com/configure_entities"
                        + "?instance_id=" + instance_id
                        + "&install_id=" + install_id
                        + "&user_name=" + user_name
                        + "&user_id=" + user_id
                        + "&site_name=" + site_name
                        + "&site_id=" + site_id
                        + "&app_id=" + app_id
                        + "&asset_id=" + asset_id
                        + "&asset_type=" + asset_type
                        + "&asset_name=" + asset_name;
                                
                      //console.log('jsonData:', jsonData);
                                
                      try {
                        const response = await fetch(urlFetch, {
                          method: 'POST',
                          headers: {
                            'Content-Type': 'application/json'
                          },
                          body: jsonData
                        });
                                
                        const data = await response.json();
                        console.log(data);
                        alert('Template actualizado correctamente');
                      } catch (error) {
                        console.error('Error:', error);
                        alert('Hubo un error al actualizar el template. Por favor, intente nuevamente.');
                      }
                                
                    }
                                
                    // Debounce function
                    function debounce(func, wait) {
                      let timeout;
                      return function executedFunction(...args) {
                        clearTimeout(timeout);
                        timeout = setTimeout(() => func.apply(this, args), wait);
                      };
                    }
                                
                    const debouncedHighlight = debounce(highlightSyntax, 300);
                    document.getElementById('htmlEditor').addEventListener('input', debouncedHighlight);
                                
                    // Función para extraer los nombres de los campos del template
                    function extractFieldsFromTemplate(template) {
                      const regex = /{{(.*?)}}/g; // Expresión regular para encontrar {{campo}}
                      let fields = [];
                      let match;
                                
                      while ((match = regex.exec(template)) !== null) {
                        fields.push(match[1]); // Agrega el nombre del campo (lo que está dentro de {{}})
                      }
                                
                      return fields;
                    }
                                
                    // Función para reconstruir el array customObjectFields
                    function rebuildCustomObjectFields(fields, originalFields) {
                      return fields.map(fieldName => {
                        // Buscar en el array original por el campo que coincida con el nombre
                        return originalFields.find(field => field.name === fieldName);
                      });
                    }
                  </script>
                </body>
                                
                </html>
                """;

        // Crear la respuesta
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setHeaders(Map.of("Content-Type", "text/html"));
        response.setBody(htmlResponse);

        return response;
    }
}
