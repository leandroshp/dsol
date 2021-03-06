package org.dsol.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.dsol.api.management.util.Serializer;
import org.dsol.planner.api.Planner;

public class ApplicationImpl extends ResourceImpl implements Application {

	private String classpathFolder;
	private Planner planner;
	
	
	public ApplicationImpl(Planner planner, String classpathFolder) {
		this.classpathFolder = classpathFolder;
		this.planner = planner;
		initPlanner();
	}
	
	private void initPlanner(){
		try{
			InputStream initialStateStream = new ByteArrayInputStream(Planner.EMPTY_INITIAL_STATE.getBytes());
			InputStream goalStream = new ByteArrayInputStream(Planner.EMPTY_GOAL.getBytes());
			InputStream actions = new ByteArrayInputStream(new byte[0]);
			planner.initialize(actions,initialStateStream, goalStream);
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public String index() {
		return getResource("/html/management.html");
	}

	@Override
	public String instancePage(String refId) {
		return getResource("/html/instance.html");
	}

	@Override
	public String uploadJar(String refId, MultipartBody body) {
		Attachment attachment = body.getAttachment("jar_file");
		
		ContentDisposition contentDisposition = new ContentDisposition(attachment.getHeader("Content-disposition"));
		String fileName = contentDisposition.getParameter("filename");
		
		FileOutputStream f;
		try {
			File fi = new File(classpathFolder+File.separatorChar+fileName);
			f = new FileOutputStream(fi);
			IOUtils.copy(attachment.getDataHandler().getInputStream(), f);

			f.close();
			
			return "<html><head><style>body{font-family: \"Trebuchet MS\", sans-serif;font-size: 13px;}</style></head><body>Jar <b>"+fileName+"</b> uploaded successfully.</body></html>";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "<html><body>Error uploading jar. :( </body></html>";
	}
	
	@Override
	public String uploadActions(String refId, MultipartBody body) {
		Attachment attachment = body.getAttachment("actions_file");
		
		String script = "";
		try{
			InputStream is = attachment.getDataHandler().getInputStream();
			planner.loadActions(is);
			script = "var actionsLoaded = "+(Serializer.toJSON(planner))+";parent.load_from_file(actionsLoaded);";
		}
		catch(Exception ex){
			String message = ex.getMessage();
			if(message == null){
				message = "Unknown error.";
			}
			message = message.replaceAll("\n", "<br/>");
			script = "parent.show_message('Error parsing source code','"+message+"');";
		}
		return "<html><head><script>"+script+"</script></head></html>";
	}

	@Override
	public String orchestrationInterfacePage(String refId) {
		return getResource("/html/orchestrationInterface.html");
	}

	@Override
	public String servicesPage() {
		return getResource("/html/services.html");
	}
}
