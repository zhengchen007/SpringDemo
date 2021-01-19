package com.olo.ding.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.olo.ding.entity.DealTaskEntity;
import com.olo.ding.entity.HomePageEntity;
import com.olo.ding.entity.TaskDetailEntity;
import com.olo.ding.mapper.DealTaskMapper;
import com.olo.ding.mapper.RankMapper;
import com.olo.ding.mapper.TaskDistributeMapper;
import com.olo.ding.service.DealTaskService;
import com.olo.ding.utils.HttpRequester;
import com.olo.ding.utils.PjString;
import com.olo.ding.utils.SshUtil;
import com.olo.ding.utils.WeChatService;

import net.sf.json.JSONObject;

@Service("DealTaskService")
public class DealTaskServiceImpl implements DealTaskService{
	private static final String SUCCESSCODE="201";
	private static final String FILEDCODE = "501";
	static SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	@Override
	public List<Map<String, Object>> executorFileUpload(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
		String savePath = "/data/olo_file/ding/executor/"+df_time.format(new Date());
		System.out.println("savePath==="+savePath);
		//String savePath = "G:\\"+df_time.format(new Date());
		File file = new File(savePath);
		List<Map<String, Object>> fileUrl = new ArrayList<Map<String,Object>>();
		// 判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			file.mkdir();
		}
		String filePath = savePath+"/";
		File fileMission = new File(filePath);
		if (!fileMission.exists() && !fileMission.isDirectory()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			fileMission.mkdir();
		}
		// 消息提示
			// 使用Apache文件上传组件处理文件上传步骤：
			// 1、创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 2、创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			// 3、判断提交上来的数据是否是上传表单的数据
			if (!ServletFileUpload.isMultipartContent(request)) {
				// 按照传统方式获取数据
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("result", "failed");
				fileUrl.add(map);
				return fileUrl;
			}
			// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			
			List<FileItem> list = upload.parseRequest(request);
			//生成单例对象存储文件路径
			for (FileItem item : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 如果fileitem中封装的是普通输入项的数据
				if (item.isFormField()) {
					String name = item.getFieldName();
					// 解决普通输入项的数据的中文乱码问题
					String value = item.getString("UTF-8");
					// value = new String(value.getBytes("iso8859-1"),"UTF-8");
					System.out.println(name + "=" + value);
				} else {// 如果fileitem中封装的是上传文件
						// 得到上传的文件名称，
					String filename = item.getName();
					System.out.println(filename);
					if (filename == null || filename.trim().equals("")) {
						continue;
					}
					// 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
					// c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
					// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					filename = filename.substring(filename.lastIndexOf("/") + 1);
					// 获取item中的上传文件的输入流
					InputStream in = item.getInputStream();
					// 创建一个文件输出流
					FileOutputStream out = new FileOutputStream(filePath + filename);
					String fileUrlStr = "";
					//正式环境地址
					fileUrlStr = "http://crmmobile1.olo-home.com:8038" + filePath  + filename;
					//本地测试地址
					//fileUrlStr =  "H:\\apache-tomcat-9.0.8\\webapps\\EnterpriseCalendar\\upload";
					map.put("fileUrl", fileUrlStr);
					map.put("fileName", filename);
					map.put("result", "success");
					fileUrl.add(map);
					// 创建一个缓冲区
					byte buffer[] = new byte[1024];
					// 判断输入流中的数据是否已经读完的标识
					int len = 0;
					// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
					while ((len = in.read(buffer)) > 0) {
						// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\"
						// + filename)当中
						out.write(buffer, 0, len);
					}
					// 关闭输入流
					in.close();
					// 关闭输出流
					out.close();
					// 删除处理文件上传时生成的临时文件
					item.delete();
				}
			}

		return fileUrl;

	}
	@Override
	public List<Map<String, Object>> proctorFileUpload(HttpServletRequest request, HttpServletResponse response)
			throws Exception {


		// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
		String savePath = "/data/olo_file/ding/proctor/"+df_time.format(new Date());
		System.out.println("savePath==="+savePath);
		//String savePath = "G:\\"+df_time.format(new Date());
		File file = new File(savePath);
		List<Map<String, Object>> fileUrl = new ArrayList<Map<String,Object>>();
		// 判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			file.mkdir();
		}
		String filePath = savePath+"/";
		File fileMission = new File(filePath);
		if (!fileMission.exists() && !fileMission.isDirectory()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			fileMission.mkdir();
		}
		// 消息提示
			// 使用Apache文件上传组件处理文件上传步骤：
			// 1、创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 2、创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			// 3、判断提交上来的数据是否是上传表单的数据
			if (!ServletFileUpload.isMultipartContent(request)) {
				// 按照传统方式获取数据
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("result", "failed");
				fileUrl.add(map);
				return fileUrl;
			}
			// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			
			List<FileItem> list = upload.parseRequest(request);
			//生成单例对象存储文件路径
			for (FileItem item : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				// 如果fileitem中封装的是普通输入项的数据
				if (item.isFormField()) {
					String name = item.getFieldName();
					// 解决普通输入项的数据的中文乱码问题
					String value = item.getString("UTF-8");
					// value = new String(value.getBytes("iso8859-1"),"UTF-8");
					System.out.println(name + "=" + value);
				} else {// 如果fileitem中封装的是上传文件
						// 得到上传的文件名称，
					String filename = item.getName();
					System.out.println(filename);
					if (filename == null || filename.trim().equals("")) {
						continue;
					}
					// 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
					// c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
					// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					filename = filename.substring(filename.lastIndexOf("/") + 1);
					// 获取item中的上传文件的输入流
					InputStream in = item.getInputStream();
					// 创建一个文件输出流
					FileOutputStream out = new FileOutputStream(filePath + filename);
					String fileUrlStr = "";
					//正式环境地址
					fileUrlStr = "http://crmmobile1.olo-home.com:8038" + filePath  + filename;
					//本地测试地址
					//fileUrlStr =  "H:\\apache-tomcat-9.0.8\\webapps\\EnterpriseCalendar\\upload";
					map.put("fileUrl", fileUrlStr);
					map.put("fileName", filename);
					map.put("result", "success");
					fileUrl.add(map);
					// 创建一个缓冲区
					byte buffer[] = new byte[1024];
					// 判断输入流中的数据是否已经读完的标识
					int len = 0;
					// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
					while ((len = in.read(buffer)) > 0) {
						// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\"
						// + filename)当中
						out.write(buffer, 0, len);
					}
					// 关闭输入流
					in.close();
					// 关闭输出流
					out.close();
					// 删除处理文件上传时生成的临时文件
					item.delete();
				}
			}

		return fileUrl;

	
	}
	public  static boolean unZipFiles(File zipFile,String descDir,String rename)  {
		try {
			
		File pathFile = new File(descDir);
		if (!pathFile.exists()){
			pathFile.mkdirs();
		}
		ZipFile zip = new ZipFile(zipFile);
		for (Enumeration entries = zip.getEntries(); entries.hasMoreElements();){
			ZipEntry entry = (ZipEntry) entries.nextElement();
			entry.setUnixMode(644);
			String zipEntryName = entry.getName();
			System.out.println(zipEntryName);
			InputStream in = zip.getInputStream(entry);
             String outPath = (descDir + rename).replace("/", File.separator); 
             System.out.println("解压后的路径为==="+outPath);
			File file = new File(outPath.substring(0,outPath.lastIndexOf("/")));
			if (!file.exists()){
				file.mkdirs();
			}
			if (new File(outPath).isDirectory()){
				continue;
			}
			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0){
				out.write(buf1,0,len);
			}
			in.close();
			out.close();
		}
		} catch (Exception e) {
			System.out.println("解压报错==="+e);
			return false;
		}
		return true;
	}
	@Autowired
	DealTaskMapper dealTaskMapper;
	@Autowired
	RankMapper rankMapper;
	@Autowired
	TaskDistributeMapper taskDistributeMapper;
	/**
	 * 查看首页待办任务详情
	 */
	@Override
	public String queryNeedToDealTask(Integer requestId,Integer taskResource) {
		//如果任务来源是0：代表项目专项等任务
		//如果任务来源是1:代表派工单任务
		 SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> requestIdMap = new HashMap<String, Object>();
		requestIdMap.put("requestId", requestId);
		List<DealTaskEntity> zbList = new ArrayList<DealTaskEntity>();
		List<DealTaskEntity> isList =  dealTaskMapper.getTaskDetail(requestIdMap);
		if (isList.isEmpty()&&taskResource != 4) {
			Map<String, Object> parmMap = new HashMap<String, Object>();
			parmMap.put("taskId",requestId);
			List<DealTaskEntity> TaskList_uf_olo_ding_task = dealTaskMapper.queryNotCfDetail(parmMap);
			List<String> xzrList = new ArrayList<String>();
			if (!StringUtils.isEmpty(TaskList_uf_olo_ding_task.get(0).getXzr()) && TaskList_uf_olo_ding_task.get(0).getXzr()!=null) {
				xzrList = Arrays.asList(TaskList_uf_olo_ding_task.get(0).getXzr().split(","));
			}
			List<String> zxrList = new ArrayList<String>();
			if (!StringUtils.isEmpty(TaskList_uf_olo_ding_task.get(0).getZxr()) && TaskList_uf_olo_ding_task.get(0).getZxr()!=null) {
				zxrList = Arrays.asList(TaskList_uf_olo_ding_task.get(0).getZxr().split(","));
			}
			String rwfjStr = TaskList_uf_olo_ding_task.get(0).getRwfj();
			List<String> listRwfj = new ArrayList<String>();
			if (!StringUtils.isEmpty(rwfjStr)) {
				listRwfj = Arrays.asList(rwfjStr.substring(1,rwfjStr.length()-1).split(","));
			}
			TaskList_uf_olo_ding_task.get(0).setRwfjdz(listRwfj);
			List<String> list_rwnr = new ArrayList<String>();
			if (!StringUtils.isEmpty(TaskList_uf_olo_ding_task.get(0).getRwnrStr())) {
				list_rwnr = Arrays.asList(TaskList_uf_olo_ding_task.get(0).getRwnrStr().split("&"));
			}
			TaskList_uf_olo_ding_task.get(0).setRwnr(list_rwnr);
			String zxrStr = "";
			for (int i = 0; i < zxrList.size(); i++) {
				Map<String, Object> zxrIdMap = new HashMap<String, Object>();
				zxrIdMap.put("id", zxrList.get(i));
				List<TaskDetailEntity> list = taskDistributeMapper.getPeopleName(zxrIdMap);
				zxrStr = list.get(i).getLastname()+",";
			}
			
			String xzrStr = "";
			for (int i = 0; i < xzrList.size(); i++) {
				Map<String, Object> xzrIdMap = new HashMap<String, Object>();
				xzrIdMap.put("id", xzrList.get(i));
				List<TaskDetailEntity> list = taskDistributeMapper.getPeopleName(xzrIdMap);
				xzrStr = list.get(i).getLastname()+",";
			}
			if (!StringUtils.isEmpty(xzrStr)&&xzrStr!=null) {
				xzrStr = xzrStr.substring(0, xzrStr.length()-1);
			}
			TaskList_uf_olo_ding_task.get(0).setXzrXm(xzrStr);
			if (!StringUtils.isEmpty(zxrStr)&&xzrStr!=null) {
				zxrStr = zxrStr.substring(0, zxrStr.length()-1);
			}
			TaskList_uf_olo_ding_task.get(0).setZxrXm(zxrStr);
			System.out.println(TaskList_uf_olo_ding_task);
			returnMap.put("code", SUCCESSCODE);
			returnMap.put("data",TaskList_uf_olo_ding_task.get(0));
		}
		else {

			if (taskResource != 4) {
				//查询是否有执行人附件
				List<DealTaskEntity> zxrfjList = dealTaskMapper.queryZxrfj(requestIdMap);
				if (!zxrfjList.isEmpty() && zxrfjList !=null && zxrfjList.get(0)!=null) {
					String zxrfjStr = zxrfjList.get(0).getZxrfj();
					if (!StringUtils.isEmpty(zxrfjStr)&&zxrfjStr!=null) {
						List<String> zxrfj_List =  Arrays.asList(zxrfjStr.split(","));
						Map<String, Object> zxrfjMap = new HashMap<String, Object>();
						zxrfjMap.put("addr",zxrfj_List);
						List<DealTaskEntity> list_fjdz = dealTaskMapper.queryAddr(zxrfjMap);
						String pinjieUrl = "";
						//连接服务器
						SshUtil.login();
						for (int j = 0; j < list_fjdz.size(); j++) {
							String fileName = list_fjdz.get(j).getFileName();
							System.out.println("执行人文件名称为===="+fileName);
							String docid = list_fjdz.get(j).getDocid();
							System.out.println("执行人docid为===="+docid);
							System.out.println("执行人list_fjdz.get(j).getFilePath()==="+list_fjdz.get(j).getFilePath());
							System.out.println("执行人SshUtil.conn==="+SshUtil.conn);
							String localPath = "/data/olo_file/ding/zxr/"+df.format(new Date())+"/"+docid;
							File fileMission = new File(localPath);
							if (!fileMission.exists()){
								fileMission.mkdirs();
							}
							SshUtil.copyFile(SshUtil.conn, list_fjdz.get(j).getFilePath(), localPath);
							boolean result = unZipFiles(new File(localPath+list_fjdz.get(j).getFilePath().substring(list_fjdz.get(j).getFilePath().lastIndexOf("/"), list_fjdz.get(j).getFilePath().length())), "/data/olo_file/ding/zxrfjdz/"+docid+"/",fileName);
							System.out.println("执行人解压结果为===="+result);
							if (result) {
								pinjieUrl += "http://crmmobile1.olo-home.com:8038/data/olo_file/ding/zxrfjdz/"+docid+"/"+fileName+",";
							}
							if (!StringUtils.isEmpty(pinjieUrl)) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("requestId", requestId);
								map.put("zxrfj", pinjieUrl);
								dealTaskMapper.update_formtable_main_836(map);
							}
							
						}
					}
				}
				
				
				//查询是否有监督人附件
				List<DealTaskEntity> jdrfjList = dealTaskMapper.queryZxrfj(requestIdMap);
				if (!jdrfjList.isEmpty() && jdrfjList !=null && zxrfjList.get(0)!=null) {
					String jdrfjStr = jdrfjList.get(0).getJdrfj();
					if (!StringUtils.isEmpty(jdrfjStr)&&jdrfjStr!=null) {
						List<String> jdrfj_List =  Arrays.asList(jdrfjStr.split(","));
						Map<String, Object> jdrfjMap = new HashMap<String, Object>();
						jdrfjMap.put("addr",jdrfj_List);
						List<DealTaskEntity> list_fjdz_jdr = dealTaskMapper.queryAddr(jdrfjMap);
						String jdrpinjieUrl = "";
						//连接服务器
						SshUtil.login();
						for (int j = 0; j < list_fjdz_jdr.size(); j++) {
							String fileName = list_fjdz_jdr.get(j).getFileName();
							System.out.println("监督人文件名称为===="+fileName);
							String docid = list_fjdz_jdr.get(j).getDocid();
							System.out.println("监督人docid为===="+docid);
							System.out.println("监督人list_fjdz.get(j).getFilePath()==="+list_fjdz_jdr.get(j).getFilePath());
							System.out.println("监督人SshUtil.conn==="+SshUtil.conn);
							String localPath = "/data/olo_file/ding/jdr/"+df.format(new Date())+"/"+docid;
							File fileMission = new File(localPath);
							if (!fileMission.exists()){
								fileMission.mkdirs();
							}
							SshUtil.copyFile(SshUtil.conn, list_fjdz_jdr.get(j).getFilePath(), localPath);
							boolean result = unZipFiles(new File(localPath+list_fjdz_jdr.get(j).getFilePath().substring(list_fjdz_jdr.get(j).getFilePath().lastIndexOf("/"), list_fjdz_jdr.get(j).getFilePath().length())), "/data/olo_file/ding/jdrfjdz/"+docid+"/",fileName);
							System.out.println("监督人解压结果为===="+result);
							if (result) {
								jdrpinjieUrl += "http://crmmobile1.olo-home.com:8038/data/olo_file/ding/jdrfjdz/"+docid+"/"+fileName+",";
							}
							if (!StringUtils.isEmpty(jdrpinjieUrl)) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("requestId", requestId);
								map.put("jdrfj", jdrpinjieUrl);
								dealTaskMapper.update_formtable_main_836_jdrfj(map);
							}
							
						}
					}
				}
				
				
				
				
				
				
				
				zbList = dealTaskMapper.getTaskDetail(requestIdMap);
				List<String> zxr = Arrays.asList(zbList.get(0).getZxr().split(","));
				String zxrXm = "";
				for (int i = 0; i < zxr.size(); i++) {
					Map<String, Object> peopleNameMap = new HashMap<String, Object>();
					peopleNameMap.put("userId",zxr.get(i));
					zxrXm += rankMapper.getPeopleName(peopleNameMap).get(0).getLastName()+",";
				}
				zbList.get(0).setZxrXm(zxrXm.substring(0, zxrXm.length()-1));
				
				//执行人数组
				if (zbList.get(0).getZxrfjdzStr() != null && !StringUtils.isEmpty(zbList.get(0).getZxrfjdzStr())) {
					 zbList.get(0).setZxrfjdz(Arrays.asList(zbList.get(0).getZxrfjdzStr().split(",")));
				}else {
					zbList.get(0).setZxrfjdz(new ArrayList<String>());
				}
				//任务附件地址
				if (zbList.get(0).getRwfjdzStr() != null && !StringUtils.isEmpty(zbList.get(0).getRwfjdzStr())) {
					 zbList.get(0).setRwfjdz(Arrays.asList(zbList.get(0).getRwfjdzStr().split("\\|")));
				}else {
					zbList.get(0).setRwfjdz(new ArrayList<String>());
				}
				//执行人附件
		        if (!StringUtils.isEmpty(zbList.get(0).getZxrfjdzStr())&&zbList.get(0).getZxrfjdzStr()!=null) {
		        	zbList.get(0).setZxrfjdz(Arrays.asList(zbList.get(0).getZxrfjdzStr().split(",")));
		        }else {
		        	zbList.get(0).setZxrfjdz(new ArrayList<String>());
				}
				//监督人附件
		        if (!StringUtils.isEmpty(zbList.get(0).getJdrfjdzStr())&&zbList.get(0).getJdrfjdzStr()!=null) {
		        	zbList.get(0).setJdrfjdz(Arrays.asList(zbList.get(0).getJdrfjdzStr().split(",")));
		        }else {
		        	zbList.get(0).setJdrfjdz(new ArrayList<String>());
				}
				if (zbList.get(0).getXzr() != null && !StringUtils.isEmpty(zbList.get(0).getXzr())) {
					List<String> xzr = Arrays.asList(zbList.get(0).getXzr().split(","));
					String xzrXm = "";
					for (int i = 0; i < xzr.size(); i++) {
						Map<String, Object> peopleNameMap = new HashMap<String, Object>();
						peopleNameMap.put("userId",xzr.get(i));
						xzrXm += rankMapper.getPeopleName(peopleNameMap).get(0).getLastName()+",";
					}
					zbList.get(0).setXzrXm(xzrXm);
				}
				Map<String, Object> zbIdMap = new HashMap<String, Object>();
				zbIdMap.put("mainid", zbList.get(0).getId());
				List<DealTaskEntity> mxList = dealTaskMapper.getTaskDetailMx(zbIdMap);
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < mxList.size(); i++) {
					list.add(mxList.get(i).getMxRwnr());
				}
				zbList.get(0).setRwnr(list);
			}else if (taskResource == 4) {
				//返回派工单字段
				zbList = dealTaskMapper.getPgdDetail(requestIdMap);
			}
		
			returnMap.put("code", SUCCESSCODE);
			returnMap.put("data",zbList.get(0));
		}
		
		return PjString.tojSONString(returnMap);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("程序报错==="+e);
			returnMap.put("code", FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	/**
	 * 派工单流程提交
	 */
	@Override
	public String excutePgdWorkFlow(DealTaskEntity dealTaskEntity) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		bodyMap.put("requestid", Integer.parseInt(dealTaskEntity.getRequestId()));
		bodyMap.put("userId", dealTaskEntity.getUserId());
		bodyMap.put("taskResource",+dealTaskEntity.getTaskResource());
		bodyMap.put("type", "submit");
		bodyMap.put("nodeId", "");
		JSONObject jsonObject = JSONObject.fromObject(bodyMap);
		String jsonStr = jsonObject.toString();
		HttpRequester.requestPOST("http://192.168.2.85/interface/cz/jsp/dingdingWorkflowSubmit.jsp", jsonStr, "application/json;charset=utf-8");
		//WeChatService.newSendGet(dealTaskEntity.getZxr(), "您有一条新的任务，任务名称为："+dealTaskEntity.getName()+",请登录我乐钉钉及时处理。");
		//更新字段
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("thjl", dealTaskEntity.getThjl());
		map.put("csyy", dealTaskEntity.getCsyy());
		map.put("requestId", dealTaskEntity.getRequestId());
		dealTaskMapper.updatePgd(map);
		returnMap.put("code", SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			returnMap.put("code", FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	
	
	/**
	 * 任务流程处理
	 */
	@Override
	////Integer requestId,Integer userId,Integer submitType,Integer taskResource,List<> zxrfjdz,list<>jdrfjdz
	public String excuteWorkFlow(DealTaskEntity dealTaskEntity) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		//任务的执行人提交节点
		if (dealTaskEntity.getTaskResource()!=4&&dealTaskEntity.getNodeId()==90550) {
			//流程提交并且传入附件到oa
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put("requestid", Integer.parseInt(dealTaskEntity.getRequestId()));
			bodyMap.put("userId", dealTaskEntity.getUserId());
			bodyMap.put("type", "submit");
			bodyMap.put("nodeId", "90550");
			bodyMap.put("taskResource",dealTaskEntity.getTaskResource());
			bodyMap.put("zxrfjdz", dealTaskEntity.getZxrfjdzStr());
			System.out.println("执行人附件地址为==="+dealTaskEntity.getZxrfjdzStr());
			String body = PjString.pjString(bodyMap);
			//根据requestid删除zxrfj再插入新的zxrfj
			Map<String, Object> zxrfjMap = new HashMap<String, Object>();
			zxrfjMap.put("requestId", Integer.parseInt(dealTaskEntity.getRequestId()));
			dealTaskMapper.updatezxrfj(zxrfjMap);
			String rejectResult = HttpRequester.requestPOST("http://192.168.2.85/interface/cz/jsp/dingdingWorkflowSubmit.jsp", body, "application/json;charset=utf-8");
			//给监督人发送消息
			WeChatService.newSendGet(dealTaskEntity.getJdr(), "您有一条新的任务，任务名称为："+dealTaskEntity.getName()+",请登录我乐钉钉及时处理。");
			System.out.println("流程操作是否成功==="+rejectResult);
			if ("success".equals(rejectResult)) {
				//更新zxryj和zxrfjdz到主表
				Map<String, Object> zxrParmMap = new HashMap<String, Object>();
				zxrParmMap.put("requestId", dealTaskEntity.getRequestId());
				zxrParmMap.put("zxrbz", dealTaskEntity.getZxrbz());
				zxrParmMap.put("zxrfjdz", dealTaskEntity.getZxrfjdzStr());
				dealTaskMapper.updateZxrbzAndfj(zxrParmMap);
			}
		}
		//任务的监督人提交节点（提交或者退回）
		if (dealTaskEntity.getTaskResource()!=4&&dealTaskEntity.getNodeId()==90551) {
			//流程提交并且传入附件到oa
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put("requestid", Integer.parseInt(dealTaskEntity.getRequestId()));
			bodyMap.put("userId", dealTaskEntity.getUserId());
			bodyMap.put("nodeId", "90551");
			bodyMap.put("taskResource",dealTaskEntity.getTaskResource());
			bodyMap.put("jdrfjdz", dealTaskEntity.getJdrfjdzStr());
			System.out.println("监督人附件地址为==="+dealTaskEntity.getJdrfjdzStr());
			if (dealTaskEntity.getSubmitType()==0) {
				bodyMap.put("type", "submit");
			}else if(dealTaskEntity.getSubmitType()==1) {
				bodyMap.put("type", "reject");
				WeChatService.newSendGet(dealTaskEntity.getZxr(), "您有一条任务被退回，任务名称为："+dealTaskEntity.getName()+",请登录我乐钉钉及时处理。");
			}
			//根据requestid删除zxrfj再插入新的zxrfj
			Map<String, Object> jdrfjMap = new HashMap<String, Object>();
			jdrfjMap.put("requestId", Integer.parseInt(dealTaskEntity.getRequestId()));
			dealTaskMapper.updatejdrfj(jdrfjMap);
			String body = PjString.pjString(bodyMap);
			String rejectResult = HttpRequester.requestPOST("http://192.168.2.85/interface/cz/jsp/dingdingWorkflowSubmit.jsp", body, "application/json;charset=utf-8");
			System.out.println("流程操作是否成功==="+rejectResult);
			//更新jdryj和jdrfjdz到主表
			if ("success".equals(rejectResult)) {
				//更新jdryj和jdrfjdz到主表
				Map<String, Object> zxrParmMap = new HashMap<String, Object>();
				zxrParmMap.put("requestId", dealTaskEntity.getRequestId());
				zxrParmMap.put("jdryj", dealTaskEntity.getJdrbz());
				zxrParmMap.put("jdrfjdz", dealTaskEntity.getJdrfjdzStr());
				dealTaskMapper.updateJdrbzAndfj(zxrParmMap);
			}
		}
		returnMap.put("code", SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			returnMap.put("code", FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	@Override
	public String isOvertime(DealTaskEntity dealTaskEntity) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		SimpleDateFormat df_time_hour = new SimpleDateFormat("yyyy-MM-dd HH");// 设置日期格式
		try {
			//根据requestid判断任务是否当前时间已经逾期
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("requestId", dealTaskEntity.getRequestId());
			List<HomePageEntity> list = dealTaskMapper.isOverTime(map);
			Integer result = df_time_hour.parse(list.get(0).getTaskDemandTime()).compareTo(df_time_hour.parse(list.get(0).getOperateDateAndTime()))>=0?1:0;
			returnMap.put("data", result);
			returnMap.put("code", SUCCESSCODE);
			return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			returnMap.put("code", FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	
	public String updateJzrq(DealTaskEntity dealTaskEntity) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("newJzrq", dealTaskEntity.getNewJzrq());
			map.put("requestId", dealTaskEntity.getRequestId());
			dealTaskMapper.updateJzrq(map);
			returnMap.put("code", SUCCESSCODE);
			return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			returnMap.put("code", FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
		
	}
	
}
