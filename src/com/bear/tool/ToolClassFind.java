package com.bear.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 类文件检索
 * @author 陈开鸿
 */
public class ToolClassFind {

    protected static final Logger LOG = Logger.getLogger(ToolClassFind.class);
    
    @SuppressWarnings("rawtypes")
	private Class target;
    private String classpath;
    private boolean includeAllJarsInLib = false;
    private List<String> includeJars = new ArrayList<String>();;
    private String libDir;

    @SuppressWarnings("rawtypes")
	private ToolClassFind(Class target, String classpath, String libDir, List<String> includeJars, boolean includeAllJarsInLib) {
        this.target = target;
        this.classpath = classpath;
        this.libDir = libDir;
        this.includeJars = includeJars;
        this.includeAllJarsInLib = includeAllJarsInLib;
    }
    
    /**
     * 建造ToolClassSearcher并初始化参数,当classpath或libDir为null时,表示不做该类型的类查找,但它们不能同时为空
     * @param target		匹配的类类型
     * @param classpath		查找的类路径
     * @param libDir		查找的lib路径
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static ToolClassFind of(Class target, String classpath, String libDir, List<String> includeJars) {
        return new ToolClassFind(target, classpath, libDir, includeJars, false);
    }
    @SuppressWarnings("rawtypes")
   	public static ToolClassFind of(Class target, String classpath, String libDir, boolean includeAllJarsInLib) {
        return new ToolClassFind(target, classpath, libDir, null, includeAllJarsInLib);
    }
    @SuppressWarnings("rawtypes")
	public static ToolClassFind of(Class target, String classpath, String libDir, String ...includeJars) {
    	List<String> jarList = new ArrayList<String>();
    	for(String s : includeJars){
    		jarList.add(s);
    	}
        return new ToolClassFind(target, classpath, libDir, jarList, false);
    }
    @SuppressWarnings("rawtypes")
   	public static ToolClassFind of(Class target, String classpath) {
        return new ToolClassFind(target, classpath, null, null, false);
    }
    @SuppressWarnings("rawtypes")
   	public static ToolClassFind of(Class target, String libDir, List<String> includeJars) {
        return new ToolClassFind(target, null, libDir, includeJars, false);
    }
    @SuppressWarnings("rawtypes")
   	public static ToolClassFind of(Class target, String libDir, String ...includeJars) {
    	List<String> jarList = new ArrayList<String>();
    	for(String s : includeJars){
    		jarList.add(s);
    	}
       return new ToolClassFind(target, null, libDir, jarList, false);
    }
    @SuppressWarnings("rawtypes")
   	public static ToolClassFind of(Class target, String libDir, boolean includeAllJarsInLib) {
        return new ToolClassFind(target, null, libDir, null, includeAllJarsInLib);
    }

    /**
     * 验证类路径是否需要扫描
     * @param classFile 从文件中扫描出来的class
     * @param scanList  需要扫描的类路径
     * @return
     */
    public static boolean valiScan(String classFile, String ...scanList){
        for (String scanPath : scanList) {
        	if(classFile.startsWith(scanPath)){
        		return true;
        	}
        }
        return false;
    }

    /**
     * 递归查找文件
     * 
     * @param baseDirName
     *            查找的文件夹路径
     * @param targetFileName
     *            需要查找的文件名
     */
    public static List<String> findFiles(String baseDirName, String targetFileName) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<String> classFiles = new ArrayList<String>();
        String tempName = null;
        Pattern pattern = Pattern.compile(targetFileName);
        // 判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            LOG.error("search error：" + baseDirName + "is not a dir！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(baseDirName + File.separator + filelist[i]);
                if (readfile.isDirectory()) {
                    classFiles.addAll(findFiles(baseDirName + File.separator + filelist[i], targetFileName));
                } else {
                    tempName = readfile.getName();
                    if (pattern.matcher(tempName).matches()) {
                        String classname;
                        String temp = readfile.getAbsoluteFile().toString().replaceAll("\\\\", "/");
                        
                        if(temp.contains("/classes")){
                        	classname = temp.substring(temp.indexOf("/classes") + "/classes".length() + 1, 
                        			                   temp.indexOf(".class"));
                        }else if(temp.contains("/bin")){
                        	classname = temp.substring(temp.indexOf("/bin") + "/bin".length() + 1, 
     			                   					   temp.indexOf(".class"));
                        }else{
                        	throw new RuntimeException("ClassPath is not defind! Path: " + temp);
                        }
                        classFiles.add(classname.replaceAll("/", "."));
                    }
                }
            }
        }
        return classFiles;
    }

    /**
     * 查找jar包中的class
     * 
     * @param baseDirName
     *            jar路径
     * @param includeJars
     * @param jarFileURL
     *            jar文件地址 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     */
    private List<String> findjarFiles(String baseDirName, final List<String> includeJars) {
        List<String> classFiles = new ArrayList<String>();;
        try {
            // 判断目录是否存在
            File baseDir = new File(baseDirName);
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                LOG.error("file serach error：" + baseDirName + " is not a dir！");
            } else {
                String[] filelist = baseDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return includeAllJarsInLib || includeJars.contains(name);
                    }
                });
                for (int i = 0; i < filelist.length; i++) {
                    JarFile localJarFile = new JarFile(new File(baseDirName + File.separator + filelist[i]));
                    Enumeration<JarEntry> entries = localJarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String entryName = jarEntry.getName();
                        if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                            String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
                            classFiles.add(className);
                        }
                    }
                    localJarFile.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;

    }

    /**
     * 抓取路径下所有符合规则的class文件,并返回对应的class,根据scanPath进行过滤
     * @param scanPath 类路径: com.bear.tool
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> List<Class<? extends T>> search(String ...scanPath) {
    	List<String> classFileList = new ArrayList<String>();
    	if(!ToolString.isEmpty(classpath)){
    		classFileList.addAll(findFiles(classpath, "\\w*.class"));
    	}
    	if(!ToolString.isEmpty(libDir)){
    		classFileList.addAll(findjarFiles(libDir, includeJars));
    	}
        
        return extraction(target, classFileList, scanPath);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> List<Class<? extends T>> extraction(Class<T> clazz, List<String> classFileList, String ...scanList) {
        List<Class<? extends T>> classList = new ArrayList<Class<? extends T>>();
        for (String classFile : classFileList) {
        	if(!ToolList.isEmpty(scanList) && !valiScan(classFile, scanList)){
        		continue;
        	}
        	
            Class<?> classInFile = ToolReflect.on(classFile).get();
            if (clazz.isAssignableFrom(classInFile) && clazz != classInFile) {
                classList.add((Class<? extends T>) classInFile);
            }
        }

        return classList;
    }
}
