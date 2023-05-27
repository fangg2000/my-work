package com.xclj.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioUtil {
	

	private static final String FFMPEG_EXE = "D:/tools/ffmpeg/bin/ffmpeg.exe";

	private static ExecutorService pool = Executors.newFixedThreadPool(1);
	
//	public static void main(String[] args) throws Exception {
//		cutAudio("E:/bk_voice/record_voiceE02/Track4491.wav", "00:00:00", "00:00:1.283", "C:/Temp/test.wav");
//	}
	

	/**
     * 切割音频
     * 
     * <pre>ffmpeg -i Track4491.wav -ss 00:00:1.283 -to 00:00:4.98 -c copy cut01.wav
     * </pre>
     */
    public static boolean cutAudio(final String inputPath, final String startTime, final String endTime, 
    		final String outputPath, final boolean finalFlag) throws Exception {
    	// 设置2秒睡眠
		//TimeUnit.SECONDS.sleep(2);
		List<String> command = new ArrayList<String>();
    	command.add(FFMPEG_EXE);
    	
    	// 声音文件
    	command.add("-i");
    	command.add(inputPath);
    	
    	// 采码率
    	//command.add("-acodec");
    	//command.add("pcm_s16le");
    	
    	// 通道（单通道）
    	//command.add("-ac");
    	//command.add(channel);
    	
    	// 频率
    	//command.add("-ar");
    	//command.add(hzNum);
    	
    	// 开始时间点
    	command.add("-ss");
    	command.add(startTime);
    	
    	// 结束时间点
    	if (finalFlag == false) {
        	command.add("-to");
        	command.add(endTime);
		}
    	
    	
    	command.add("-c");
    	command.add("copy");
    	
    	// 转换音频参数设置
    	command.add(outputPath);

    	return ffCommandRun(command, 3);
    }

	
	/**
     * 转换音频
     * 
     * <pre>ffmpeg -i 0003 -acodec pcm_s16le -ac 1 -ar 22050 test000.wav
     * </pre>
     */
    public static void changeAudio(String voiceInputPath, String channel, String hzNum, String outputPath) throws Exception {
    	List<String> command = new ArrayList<String>();
    	command.add(FFMPEG_EXE);
    	
    	// 声音文件
    	command.add("-i");
    	command.add(voiceInputPath);
    	
    	// 采码率
    	command.add("-acodec");
    	command.add("pcm_s16le");
    	
    	// 通道（单通道）
    	command.add("-ac");
    	command.add(channel);
    	
    	// 频率
    	command.add("-ar");
    	command.add(hzNum);
    	
    	// 转换音频参数设置
    	command.add(outputPath);
    	
    	// for (String c : command) {
    	// System.out.print(c + " ");
    	// }
    	
    	ffCommandRun(command, 0);
    }

    /**
     * 命令执行
     */
	private static boolean ffCommandRun(List<String> command, final int seconds) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
        final Process process = builder.start();

        //InputStream errorStream = process.getErrorStream();
        //InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        //final BufferedReader br = new BufferedReader(inputStreamReader);
        
        /*new Thread() {
            @Override
            public void run() {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;

                try {
                    while ((line = in.readLine()) != null) {
                    	System.out.println("###"+line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();*/

        new Thread() {
			@Override
			public void run() {
				BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line = null;
				// StringBuilder result=new StringBuilder();
				try {
					while ((line = err.readLine()) != null) {
						System.out.println("---" + line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						err.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		try {
			int result = process.waitFor();
			if (result == 0) {
				return true;
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
			System.out.println("音频切割失败");
		}
        
        // 定义线程
// 		Callable call = null;
// 		
// 		if (seconds > 0) {
// 			call = new Callable() {
// 	 			public String call() throws Exception {
//					BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//					String line = null;
//					// StringBuilder result=new StringBuilder();
//					try {
//						while ((line = err.readLine()) != null) {
//							System.out.println("---" + line);
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//					} finally {
//						try {
//							err.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
// 	 				return "音频切割完成";
// 	 			}
// 	 		};
//		} else {
//			
//		}
//
// 		try {
// 			if (seconds > 0) {
// 	 	 		// 手动控制线程
// 	 	 		Future result = pool.submit(call);
// 	 			// 控制3秒内结束
// 	 			Object callResult = result.get(seconds, TimeUnit.SECONDS);
// 	 			//System.out.println(callResult);
//			}
//
//			int result = process.waitFor();
//			if (result == 0) {
//				return true;
//			}
// 		} catch (InterruptedException e) {
// 			System.out.println("音频切割异常");
// 		} catch (ExecutionException e) {
// 			System.out.println("音频切割异常");
// 		} catch (TimeoutException e) {
// 			System.out.println("音频切割失败");
// 		}
 		
 		return false;
	}
}
