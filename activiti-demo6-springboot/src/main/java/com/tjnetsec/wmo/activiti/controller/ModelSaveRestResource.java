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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@RestController
@RequestMapping("service")
public class ModelSaveRestResource implements ModelDataJsonConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

  @Autowired
  private RepositoryService repositoryService;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.PUT)
  @ResponseStatus(value = HttpStatus.OK)
  public void saveModel(@PathVariable String modelId, String name, String description, String json_xml, String svg_xml) {
    try {      
      Model model = repositoryService.getModel(modelId);      
      ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());     
      modelJson.put(MODEL_NAME, name);
      modelJson.put(MODEL_DESCRIPTION, description);
      model.setMetaInfo(modelJson.toString());
      model.setName(name);
      
      repositoryService.saveModel(model);
      
      repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));
      
      InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
      TranscoderInput input = new TranscoderInput(svgStream);
      
      PNGTranscoder transcoder = new PNGTranscoder();
      // Setup output
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      TranscoderOutput output = new TranscoderOutput(outStream);
      
      // Do the transformation
      transcoder.transcode(input, output);
      final byte[] result = outStream.toByteArray();
      repositoryService.addModelEditorSourceExtra(model.getId(), result);
      outStream.close();
      
      //清除
      clear();
      //发布
      deploy(modelId);
      
    } catch (Exception e) {
      LOGGER.error("Error saving model", e);
      throw new ActivitiException("Error saving model", e);
    }
  }
  
  public void clear(){
    List<Deployment> deployments = repositoryService.createDeploymentQuery().listPage(0, 100);
    for(Deployment deployment: deployments){
      	repositoryService.deleteDeployment(deployment.getId(),false);
    }
  }
  
  public void deploy(String modelId) throws JsonProcessingException, IOException{
      //获取模型
      Model modelData = repositoryService.getModel(modelId);
      byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

      if (bytes == null) {
    	  throw new ActivitiException("模型数据为空，请先设计流程并成功保存，再进行发布。");
      }
      JsonNode modelNode = new ObjectMapper().readTree(bytes);

      BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
      if(model.getProcesses().size()==0){
    	  throw new ActivitiException("数据模型不符要求，请至少设计一条主线流程。");
      }
      byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
      //发布流程
      String processName = modelData.getName() + ".bpmn20.xml";
      Deployment deployment = repositoryService.createDeployment()
              .name(modelData.getName())
              .addString(processName, new String(bpmnBytes, "UTF-8"))
              .deploy();
      modelData.setDeploymentId(deployment.getId());
      repositoryService.saveModel(modelData);
  }
}
