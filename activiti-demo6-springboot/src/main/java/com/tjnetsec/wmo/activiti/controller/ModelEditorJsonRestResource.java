/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tjnetsec.wmo.activiti.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("service")
public class ModelEditorJsonRestResource implements ModelDataJsonConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);
  
  @Autowired
  private RepositoryService repositoryService;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @RequestMapping(value="/model/{modelId}/json", method = RequestMethod.GET, produces = "application/json")
  public ObjectNode getEditorJson(@PathVariable String modelId) {
    ObjectNode modelNode = null;
    
    Model model = repositoryService.getModel(modelId);
      
    if (model != null) {
      try {
    	String str = "";
        if (StringUtils.isNotEmpty(model.getMetaInfo())) {
          modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
          str = new String(repositoryService.getModelEditorSource(model.getId()), "utf-8");
        } else {
          modelNode = objectMapper.createObjectNode();
          modelNode.put(MODEL_NAME, model.getName());
        }
        modelNode.put(MODEL_ID, model.getId());
        ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(str);
        modelNode.put("model", editorJsonNode);
        
      } catch (Exception e) {
        LOGGER.error("Error creating model JSON", e);
        throw new ActivitiException("Error creating model JSON", e);
      }
    }
    return modelNode;
  }
  
  public String getStr(String modelId){
	  StringBuilder sb = new StringBuilder();
	  sb.append("{");
	  sb.append("	\"resourceId\": \""+modelId+"\",");
	  sb.append("	\"properties\": {");
	  sb.append("		\"process_id\": \"process\",");
	  sb.append("		\"name\": \"\",");
	  sb.append("		\"documentation\": \"\",");
	  sb.append("		\"process_author\": \"\",");
	  sb.append("		\"process_version\": \"\",");
	  sb.append("		\"process_namespace\": \"http://www.activiti.org/processdef\",");
	  sb.append("		\"executionlisteners\": \"\",");
	  sb.append("		\"eventlisteners\": \"\",");
	  sb.append("		\"signaldefinitions\": \"\",");
	  sb.append("		\"messagedefinitions\": \"\"");
	  sb.append("	},");
	  sb.append("	\"stencil\": {");
	  sb.append("		\"id\": \"BPMNDiagram\"");
	  sb.append("	},");
	  sb.append("	\"childShapes\": [{");
	  sb.append("		\"resourceId\": \"sid-3C2F5302-33F3-49E2-B2A1-BEC1033A5AFF\",");
	  sb.append("		\"properties\": {");
	  sb.append("			\"overrideid\": \"\",");
	  sb.append("			\"name\": \"开始\",");
	  sb.append("			\"documentation\": \"\",");
	  sb.append("			\"executionlisteners\": \"\",");
	  sb.append("			\"initiator\": \"\",");
	  sb.append("			\"formkeydefinition\": \"\",");
	  sb.append("			\"formproperties\": \"\"");
	  sb.append("		},");
	  sb.append("		\"stencil\": {");
	  sb.append("			\"id\": \"StartNoneEvent\"");
	  sb.append("		},");
	  sb.append("		\"childShapes\": [],");
	  sb.append("		\"outgoing\": [{");
	  sb.append("			\"resourceId\": \"sid-29EC2C52-CB0E-45B9-B287-935288DD86F1\"");
	  sb.append("		}],");
	  sb.append("		\"bounds\": {");
	  sb.append("			\"lowerRight\": {");
	  sb.append("				\"x\": 207.5,");
	  sb.append("				\"y\": 242");
	  sb.append("			},");
	  sb.append("			\"upperLeft\": {");
	  sb.append("				\"x\": 177.5,");
	  sb.append("				\"y\": 212");
	  sb.append("			}");
	  sb.append("		},");
	  sb.append("		\"dockers\": []");
	  sb.append("	}, {");
	  sb.append("		\"resourceId\": \"sid-2BE44B38-0321-42FF-BC50-47B53E0F30C4\",");
	  sb.append("		\"properties\": {");
	  sb.append("			\"overrideid\": \"assgin\",");
	  sb.append("			\"name\": \"事件分配\",");
	  sb.append("			\"documentation\": \"\",");
	  sb.append("			\"asynchronousdefinition\": \"false\",");
	  sb.append("			\"exclusivedefinition\": \"false\",");
	  sb.append("			\"executionlisteners\": \"\",");
	  sb.append("			\"multiinstance_type\": \"None\",");
	  sb.append("			\"multiinstance_cardinality\": \"\",");
	  sb.append("			\"multiinstance_collection\": \"\",");
	  sb.append("			\"multiinstance_variable\": \"\",");
	  sb.append("			\"multiinstance_condition\": \"\",");
	  sb.append("			\"isforcompensation\": \"false\",");
	  sb.append("			\"usertaskassignment\": \"\",");
	  sb.append("			\"formkeydefinition\": \"\",");
	  sb.append("			\"duedatedefinition\": \"\",");
	  sb.append("			\"prioritydefinition\": \"\",");
	  sb.append("			\"formproperties\": \"\",");
	  sb.append("			\"tasklisteners\": \"\"");
	  sb.append("		},");
	  sb.append("		\"stencil\": {");
	  sb.append("			\"id\": \"UserTask\"");
	  sb.append("		},");
	  sb.append("		\"childShapes\": [],");
	  sb.append("		\"outgoing\": [],");
	  sb.append("		\"bounds\": {");
	  sb.append("			\"lowerRight\": {");
	  sb.append("				\"x\": 352.5,");
	  sb.append("				\"y\": 267");
	  sb.append("			},");
	  sb.append("			\"upperLeft\": {");
	  sb.append("				\"x\": 252.5,");
	  sb.append("				\"y\": 187");
	  sb.append("			}");
	  sb.append("		},");
	  sb.append("		\"dockers\": []");
	  sb.append("	}, {");
	  sb.append("		\"resourceId\": \"sid-29EC2C52-CB0E-45B9-B287-935288DD86F1\",");
	  sb.append("		\"properties\": {");
	  sb.append("			\"overrideid\": \"\",");
	  sb.append("			\"name\": \"\",");
	  sb.append("			\"documentation\": \"\",");
	  sb.append("			\"conditionsequenceflow\": \"\",");
	  sb.append("			\"executionlisteners\": \"\",");
	  sb.append("			\"defaultflow\": \"false\"");
	  sb.append("		},");
	  sb.append("		\"stencil\": {");
	  sb.append("			\"id\": \"SequenceFlow\"");
	  sb.append("		},");
	  sb.append("		\"childShapes\": [],");
	  sb.append("		\"outgoing\": [{");
	  sb.append("			\"resourceId\": \"sid-2BE44B38-0321-42FF-BC50-47B53E0F30C4\"");
	  sb.append("		}],");
	  sb.append("		\"bounds\": {");
	  sb.append("			\"lowerRight\": {");
	  sb.append("				\"x\": 251.65625,");
	  sb.append("				\"y\": 227");
	  sb.append("			},");
	  sb.append("			\"upperLeft\": {");
	  sb.append("				\"x\": 208.109375,");
	  sb.append("				\"y\": 227");
	  sb.append("			}");
	  sb.append("		},");
	  sb.append("		\"dockers\": [{");
	  sb.append("			\"x\": 15,");
	  sb.append("			\"y\": 15");
	  sb.append("		}, {");
	  sb.append("			\"x\": 50,");
	  sb.append("			\"y\": 40");
	  sb.append("		}],");
	  sb.append("		\"target\": {");
	  sb.append("			\"resourceId\": \"sid-2BE44B38-0321-42FF-BC50-47B53E0F30C4\"");
	  sb.append("		}");
	  sb.append("	}],");
	  sb.append("	\"bounds\": {");
	  sb.append("		\"lowerRight\": {");
	  sb.append("			\"x\": 1200,");
	  sb.append("			\"y\": 1050");
	  sb.append("		},");
	  sb.append("		\"upperLeft\": {");
	  sb.append("			\"x\": 0,");
	  sb.append("			\"y\": 0");
	  sb.append("		}");
	  sb.append("	},");
	  sb.append("	\"stencilset\": {");
	  sb.append("		\"url\": \"stencilsets/bpmn2.0/bpmn2.0.json\",");
	  sb.append("		\"namespace\": \"http://b3mn.org/stencilset/bpmn2.0#\"");
	  sb.append("	},");
	  sb.append("	\"ssextensions\": []");
	  sb.append("}");
	  return sb.toString();
  }
}
