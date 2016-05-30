package repast.simphony.systemdynamics.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Properties;


    public class SDModelWithPropertiesVDM_Native extends SDModel {
        
        public static Properties PROPERTIES = new Properties();
        private static String DEFAULT_PROPERTIES = "DefaultRunner.properties";
        
        protected LoggerJava logger = new LoggerJava();
        
        public static String SEPARATOR = "\t";
        public static final boolean trace = false;
        private String lastVar = "";
        
        
        protected VDM vdm = null;
        
        public SDModelWithPropertiesVDM_Native(String name, boolean logit, String[] args) {
    	super(name, args);
    	
    	if (args != null && args.length > 0)
    	    loadProperties(args[0]);
    	else
    	    loadProperties();
    	OUTPUT_DIRECTORY = PROPERTIES.getProperty("outputDirectory");
    	DATA_DIRECTORY = PROPERTIES.getProperty("dataDirectory");
    	
    	if (logit) {
    		logger.setLogFile(name+"Log.tab");
    		logger.log("Variable"+SEPARATOR+"Time"+SEPARATOR+"Value");
    	}
    	
        }

        @Override
        public void execute() {
        	
        	System.out.println("SDModelWithPropertiesVDM_Native: execute()");
            
//            logger.setLogFile(name+"Log.csv");

    	Initializer.initialize();
    	initializeVDM();
    	if (vdm != null)
    	    vdm.advanceTime();
    	
    	    oneTime();
    	    int tick = 0;
    	    for (double time = getINITIALTIME(); time <= getFINALTIME(); time += getTIMESTEP()) {
    	         data.setCurrentTime(time);
    	         currentTime = time;
    	         Synchronizer.synchronize(currentTime, getTIMESTEP());
    	         repeated(time, getTIMESTEP());
    	         reportTimeStep(time);
    	         
    	         if (dumpMemory) {
    	             // dump memory at the end of each time tick
    	             String filename = "_memoryDump_"+(tick++)+".csv";
    	             results.writeReport(RunnerConstants.OUTPUT_DIRECTORY+name+filename, data);
    	         }
    	         
    	         if (vdm != null)
    	 	    vdm.advanceTime();
    	    }
//    	    results.writeReport(RunnerConstants.OUTPUT_DIRECTORY+name+"_sdReport.csv", data);
    	    System.out.println("SDModelWithPropertiesVDM_Native: logger.close()");
    	    
    	    logger.close();
    	}
        
        public void finish() {
        	System.out.println("SDModelWithPropertiesVDM_Native: logger.close()");
    	    
    	    logger.close();
        }
        
//        protected void logit(String var, double time, double value) {
//        	
//        	BigDecimal bd = new BigDecimal(value);
//        	bd = bd.round(new MathContext(6));
//        	double d = bd.doubleValue();
//        	logger.log(var+","+time+","+d);
////            logger.log(var+","+time+","+value);
//        }
        
       
        
        protected void logit(String var, double time, double value, double savper) {
        	int t = (int) (time/savper);
        	double remainder = (time - (((double) t) * savper));
        	if (remainder == 0.0) {
        		if (trace) {
        			String v = var.split("\\[")[0];
        			if (!v.equals(lastVar))
        				System.out.println("log: "+var);
        			lastVar = v;
        		}
        		if (Double.isNaN(value) || Double.isInfinite(value)) {
        			logger.log(var.replace("memory.", "")+SEPARATOR+time+SEPARATOR+value);
        		} else {
        			BigDecimal bd = new BigDecimal(value);
        			bd = bd.round(new MathContext(6));
        			double d = bd.doubleValue();
        			logger.log(var.replace("memory.", "")+SEPARATOR+time+SEPARATOR+d);
        		}
        	}
        }
        
//        protected void logit(String var, double time, double value, double savper) {
//        	int t = (int) (time/savper);
//        	double remainder = (time - (((double) t) * savper));
//        	if (remainder == 0.0) {
//        		logger.log(var.replace("memory.", "")+","+time+","+value);
//        	}
//        }
        
        protected void logitVector(String var, double time, int length, double[] value) {
            logger.log(var+SEPARATOR+time+SEPARATOR+value);
        }
        
        public boolean loadProperties(String file) {
    	File props = new File(file);
    	if (props.exists()) {
    		try {
    		    PROPERTIES.load(new FileInputStream(props));
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return true;
    	} else {
    	    return false;
    	}
        }
        
        public boolean loadProperties() {
    	return loadProperties(DEFAULT_PROPERTIES);
        }
        
        private void initializeVDM() {
    	    String vdmFile = SDModelWithPropertiesVDM_Native.PROPERTIES.getProperty("vdmfile");
    	    if (vdmFile.length() > 0) {
    		vdm = new VDM(this);
    		vdm.setFile(vdmFile);
    		vdm.loadFromFile();
    	    }
    	   
    	    
    }

}
