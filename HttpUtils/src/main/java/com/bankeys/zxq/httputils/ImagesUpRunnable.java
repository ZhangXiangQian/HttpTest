package com.bankeys.zxq.httputils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class ImagesUpRunnable implements Runnable {
	private static final int TIME_OUT = 10 * 10000000; // 超时
	private static final String CHARSET = "utf-8"; // 编码
	private File file;
	private String pathUrl;
	private ImageUpUtilsCallback callback;
	private ProgressDialog pDialog;
	private int bytesRead, bytesAvailable, bufferSize;
	private int MAXbufferSize = 1024;
	private long position;
	public enum State {
		success, failure;
	}

	public ImagesUpRunnable(Context context, String filePath, String pathUrl,
			ImageUpUtilsCallback callback) {
		file = new File(filePath);
		this.pathUrl = pathUrl;
		this.callback = callback;
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("请稍候……");
		pDialog.setProgressStyle(1);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	public void uploadFile() {
		String BOUNDARY = UUID.randomUUID().toString();
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data";
		Log.i("TAG"	, "上传地址：" + pathUrl);
		try {
			URL url = new URL(pathUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			Log.i("TAG","上传图片的绝对路径：" + (file.getAbsolutePath()));
			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并上传
				 */
				OutputStream outputSteam = conn.getOutputStream();
				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * name里面的值为服务器端需要key，并且这个key才可以得到对应的文件 filename是文件的名字，包含后缀名的
				 * 比如abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				System.out.println("-----sb----->" + sb.toString());
				dos.write(sb.toString().getBytes());
				FileInputStream is = new FileInputStream(file);
				bytesAvailable = is.available();
				bufferSize = Math.min(bytesAvailable, MAXbufferSize);
				byte[] bytes = new byte[bufferSize];
				bytesRead = is.read(bytes, 0, bufferSize);
				while (bytesRead > 0) {
					dos.write(bytes, 0, bufferSize);
					position += bufferSize;
					pDialog.setProgress((int)((position * 100) / file.length()));
					bytesAvailable = is.available();
					bufferSize = Math.min(bytesAvailable, MAXbufferSize);
					bytesRead = is.read(bytes, 0, bufferSize);
					Log.i("TAG", "position:" + position + ";bytesRead:" + bytesRead + ";SizeOfFile:" + file.length());
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200 成功
				 */
				int res = conn.getResponseCode();
				Log.i("TAG", "requestCode:" + res);
				if (res == 200) {
					pDialog.dismiss();
					callback.callback(State.success);
					return;
				}
			}
		} catch (MalformedURLException e) {
			Log.i("MalformedURLException:", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("IOException:", e.getMessage());
			e.printStackTrace();
		}
		pDialog.dismiss();
		callback.callback(State.failure);
	}

	public interface ImageUpUtilsCallback {
		public void callback(State position);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
		uploadFile();
		Looper.loop();
	}

}
