import org.activiti.bpmn.model.Task;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ProcessTest {
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    @Test
        public void deployementProcessDefinitionByzip(){
            //从classpath路径下读取资源文件
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/activiti.zip");
            ZipInputStream zipInputStream = new ZipInputStream(in);
            Deployment deployment = processEngine.getRepositoryService()//获取流程定义和部署对象相关的Service
                    .createDeployment()//创建部署对象
                    .addZipInputStream(zipInputStream)//使用zip方式部署，将helloworld.bpmn和helloworld.png压缩成zip格式的文件
                    .deploy();//完成部署
            System.out.println("部署ID："+deployment.getId());//1
            System.out.println("部署时间："+deployment.getDeploymentTime());
        }

}
