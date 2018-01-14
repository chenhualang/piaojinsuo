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
            //��classpath·���¶�ȡ��Դ�ļ�
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/activiti.zip");
            ZipInputStream zipInputStream = new ZipInputStream(in);
            Deployment deployment = processEngine.getRepositoryService()//��ȡ���̶���Ͳ��������ص�Service
                    .createDeployment()//�����������
                    .addZipInputStream(zipInputStream)//ʹ��zip��ʽ���𣬽�helloworld.bpmn��helloworld.pngѹ����zip��ʽ���ļ�
                    .deploy();//��ɲ���
            System.out.println("����ID��"+deployment.getId());//1
            System.out.println("����ʱ�䣺"+deployment.getDeploymentTime());
        }

}
