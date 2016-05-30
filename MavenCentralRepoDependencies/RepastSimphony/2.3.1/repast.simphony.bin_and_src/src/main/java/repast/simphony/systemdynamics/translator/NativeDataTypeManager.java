package repast.simphony.systemdynamics.translator;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import repast.simphony.data2.AggregateOp;
import repast.simphony.data2.engine.DataSetDescriptor;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.systemdynamics.support.ArrayReference;
import repast.simphony.systemdynamics.support.MutableBoolean;
import repast.simphony.systemdynamics.support.MutableInteger;

import com.thoughtworks.xstream.XStream;

public class NativeDataTypeManager {

	private  final String[] SYSTEM_INITIAL = {"INITIAL TIME", "INITIALTIME", "INTIAL_TIME"};
	private  final String[] SYSTEM_FINAL = {"FINAL TIME", "FINALTIME", "FINAL_TIME"};
	private  final String[] SYSTEM_TIMESTEP = {"TIME STEP", "TIMESTEP", "TIME_STEP"};

	public  Set<String> scalars;
	// key = Vensim name, value = Java name
	public  Map<String, String> legal;
	public  Map<String, String> original;
	public  Map<String, String> dataType;
	// key = Vensim name
	public  Map<String, NativeArray> arrays;

	public  Set<ArrayDeclaration> arrayDeclarations = new HashSet<ArrayDeclaration>();
	
	private int getterFile = 1;

	public NativeDataTypeManager() {
		scalars = new HashSet<String>();
		// key = Vensim name, value = Java name
		legal = new HashMap<String, String>();
		original = new HashMap<String, String>();
		dataType = new HashMap<String, String>();
		// key = Vensim name
		arrays = new HashMap<String, NativeArray>();

		arrayDeclarations = new HashSet<ArrayDeclaration>();
	}
	
	public  OperationResult validateScalarReference(Map<String, Equation> equations, Equation equation, MutableInteger pos, MutableBoolean lhs) {
		OperationResult or = new OperationResult();
		String token = equation.getTokens().get(pos.value());
		String original = getOriginalName(token);
		
		if (scalars.contains(original))
			return or;
		
		if (arrays.containsKey(original)) {
			if (InformationManagers.getInstance().getArrayManager().isUsedAsLookup(original)) {
				return or;
			}
			or.setErrorMessage("Array referenced as Scalar - "+token+" - original name "+original);
			return or;
		}
		
		if (equations.containsKey(original))
			return or;
		
		or.setErrorMessage("Scalar not found - "+token+" - original name "+original);
		return or;
		
	}
	
	public NativeArray getNativeArray(String array) {
		return arrays.get(array);
	}

	public  void addVariable(Equation equation, String variable, String dataType) {

		this.dataType.put(variable, dataType);
		if (ArrayReference.isArrayReference(variable))
			addArray(equation, variable, dataType);
		else
			addScalar(variable, dataType);
	}

	private  void addScalar(String scalar, String dataType) {
		if (arrays.containsKey(scalar)) {
			System.out.println("#####Attempting to add scalar already defined as array: "+scalar);
			System.out.println("Using array definition");
			return;
		}

		scalars.add(scalar);
		String legalized = legalize(scalar);
		legal.put(scalar, legalized);
		legal.put(legalized, legalized);
		original.put(legalized, scalar);
	}

	private  void addArray(Equation equation, String arrayRef, String dataType) {
		ArrayReference ar = new ArrayReference(arrayRef);
		String name = ar.getArrayName();
		NativeArray na = new NativeArray(name);

		if (scalars.contains(name)) {
			System.out.println("Attempting to add array already defined as scalar: "+name);
			System.out.println("Remove from scalars");
			scalars.remove(name);
		}

		arrays.put(name, na);
		String legalized = legalize(name);
		legal.put(name, legalized);
		legal.put(legalized, legalized);
		original.put(legalized, name);
		int lookupHiddenDimensions = 0;
		if (equation != null && equation.isDefinesLookupGetXls())
			lookupHiddenDimensions = 2;
		na.setNumDimensions(ar.getSubscripts().size()+lookupHiddenDimensions);
		int s = 0;
		for (String subscript : ar.getSubscripts()) {
			na.setDimensionName(s++, subscript);
		}
	}

	private  String legalize(String name) {
		String legalName = null;
		legalName = makeLegal(name);
		if (Parser.isNumber(Parser.characterAt(legalName, new MutableInteger(0))))
			legalName = "_" + legalName;
		if (legal.containsValue(legalName)) {
			String col = collision(legalName);
			legal.put(name, col);
			original.put(col, name);
		} else {
			legal.put(name, legalName);
			original.put(legalName, name);
		}

		return legalName;
	}
	
	public static String getAsJavaLocalVariable(String var) {
		
//		System.out.println("getAsJavaLocalVariable "+var);
		
		// remove annotations that may have been added (e.g. memory. lookup. etc)
		
		return var.replace("memory.", "").replace("lookup.", "");
		
	}

	public  String makeLegal(String name) {
		return name.replace(" ", "_").replace("/", "_")
				.replace(":", "_").replace("-", "_").replace("&", "_")
				.replace("(", "_").replace(")", "_")
				.replace("(", "_").replace(")", "_").replace(".", "_").replace(",", "_").replace("'", "").replace("*", "_");
	}

	private  String collision(String name) {
		String aPossible = name + "_";
		while (legal.containsValue(aPossible)) {
			aPossible += "_";
		}
		return aPossible;
	}
	
	public String quoteIfNecessary(String vensimVariable) {
		
		// allow the operators to pass through
		if (Parser.isOperator(vensimVariable)) {
//			System.out.println("========== quoted in/out (operator) ///"+vensimVariable+"/// "+vensimVariable);
			return vensimVariable;
		}
		
		if (vensimVariable.contains("/") ||
				vensimVariable.contains(":") ||
				vensimVariable.contains("-") ||
				vensimVariable.contains("&") ||
				vensimVariable.contains("+") ||
				vensimVariable.contains("*") ||
				vensimVariable.contains(".") ||
				vensimVariable.contains("(") ||
				vensimVariable.contains(")") ) {
			String quoted = "\""+ vensimVariable + "\"";
			
			if (quoted.contains("[")) {
				quoted = quoted.replace("]\"", "]").replace("[", "\"[");
			}
//			System.out.println("========== quoted in/out ///"+vensimVariable+"/// "+quoted);
			return quoted;
		} else {
//			System.out.println("========== quoted in/out (no change)///"+vensimVariable+"/// "+vensimVariable);
			return vensimVariable;
		}
		
	}

	public  String getOriginalName(String legalized) {
		if (legalized.startsWith("array."))
			return legalized.replace("array.", "");
		String s = legalized.replace("memory.", "").replace("lookup.", "");
		if (original.containsKey(s))
			return original.get(s);
		else
			return legalized;
	}
	
	public  String getOriginalNameQuotedIfNecessary(String legalized) {
		if (legalized.startsWith("array."))
			return quoteIfNecessary(legalized.replace("array.", ""));
		String s = legalized.replace("memory.", "").replace("lookup.", "");
		if (original.containsKey(s))
			return quoteIfNecessary(original.get(s));
		else
			return quoteIfNecessary(legalized);
	}

	public  String getLegalName(String variable) {
		return getLegalName(null, variable);
	}

	public  String getLegalName(Equation equation, String variable) {
		String referenceType = "memory.";
		if (variable.contains("memory."))
			referenceType = "memory.";
		else if (variable.contains("lookup."))
			referenceType = "lookup.";
		String in = variable.replace("memory.", "").replace("lookup.", "");
		if (ArrayReference.isArrayReference(in)) {
			in = new ArrayReference(in).getArrayName();
//			System.out.println("Array Reference: "+variable+" /// "+in);
		}
		String name = legal.get(in);
		if (name == null) {
			if (Parser.isInteger(in) || Parser.isReal(in)) {
				return in;
			}
			// we haven't seen this yet
			if (in.equals("array")) {
				new Exception("Stack trace").printStackTrace();
			}
//			System.out.println("First getLegalName Legal -> <"+in+"><"+name+">");
			addVariable(equation, variable, "double");
		}
		name = legal.get(in);
		if (name == null) {
			if (in.equals("array")) {
				new Exception("Stack trace").printStackTrace();
			}
			// we haven't seen this yet
//			System.out.println("SEcond Legal -> <"+in+"><"+name+">");
		}

		if (Translator.target.equals(ReaderConstants.JAVA)) {
			return "memory." + name;
		} else if (Translator.target.equals(ReaderConstants.C)) {
			return "memory." + name;
		} else {
			return "memory." + name;
		}
	}

	public  void generateMemoryJava(BufferedWriter bw, String objectName, Translator translator) {

		try {
			// class header information

			//	    bw.append("package "+ReaderConstants.PACKAGE+";\n\n");
			bw.append("package "+translator.getPackageName()+";\n\n");
			bw.append("public class Memory"+objectName+" {\n");

			// scalars
			List<String> al = new ArrayList<String>();
			al.addAll(scalars);
			Collections.sort(al);
			for (String scalar : al) {

				bw.append("public "+dataType.get(scalar)+" "+legal.get(scalar)+";\n");
				//		bw.append("public  double "+legal.get(scalar)+";\n");
				//		bw.append("List<Double> "+legal.get(scalar)+"_history = new ArrayList<Double>();\n");
			}

			// arrays

			al = new ArrayList<String>();
			al.addAll(arrays.keySet());
			Collections.sort(al);
			for (String array : al) {
				bw.append("public double");
				//		bw.append("public  double");
				NativeArray na = arrays.get(array);
				for (int i = 0; i < na.getNumDimensions(); i++)
					bw.append("[]");
				bw.append(" "+legal.get(array)+";\n");
			}

			// constructor

			bw.append("public Memory"+objectName+"() {\n");

			for (String array : al) {
				if (InformationManagers.getInstance().getArrayManager().isUsedAsLookup(array))
					continue;
				int dim = 0;

				NativeArray na = arrays.get(array);
				boolean declare = false;
				for (String subscript : na.getDimensionNames()) {
					String numInd = Integer.toString(InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dim));
					if (!numInd.equals("0"))
						declare = true;
					dim++;
				}
				if (!declare)
					continue;
				bw.append(legal.get(array)+" = new double");
				dim = 0;
				for (String subscript : na.getDimensionNames()) {
					String numInd = Integer.toString(InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dim));
					if (numInd.equals("0"))
						numInd = "/* "+subscript+" */";
					bw.append("["+numInd+"]");
					dim++;
				}
				bw.append(";\n");

			}

			bw.append("}\n\n");

			if (translator.isHybridCompatibility()) {
				generateMemoryGettersSetters(bw, objectName, translator);
			} else {
				generateDataSetGetters(objectName, translator);
			}

			// HERE!!! these three getters need to be customized to what variables are use!
			
			
			bw.append("public double get_SAVEPER() {");
			bw.append("return SAVEPER;");
			bw.append("}\n");

			bw.append("public double getINITIALTIME() {");
			bw.append("return "+getInitialTimeVariable()+";");
			bw.append("}\n");

			bw.append("public double getFINALTIME() {");
			bw.append("return "+getFinalTimeVariable()+";");
			bw.append("}\n");

			bw.append("public double getTIMESTEP() {");
			bw.append("return "+getTimeStepVariable()+";");
			bw.append("}\n");

			// class tail
			bw.append("}\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public  void generateMemoryC(BufferedWriter bw, String objectName, Translator translator) {

		try {

			// scalars
			List<String> al = new ArrayList<String>();
			al.addAll(scalars);
			Collections.sort(al);
			for (String scalar : al) {

				if (InformationManagers.getInstance().getArrayManager().isUsedAsLookup(scalar)) {
					bw.append(CodeGenerator.scrub("LOOKUP_t *"));
					//		    int n = ArrayManager.getNumDimensions(scalar);
					//		    for (int i = 0; i < 2; i++)
					//			bw.append("*");
				} else {
					String t = "double";
					if (dataType.get(scalar).equals("String"))
						t = "char[256]";
					bw.append(CodeGenerator.scrub(t+" "));
				}
				bw.append(CodeGenerator.scrub(legal.get(scalar)+";\n"));
			}

			// arrays

			al = new ArrayList<String>();
			al.addAll(arrays.keySet());
			Collections.sort(al);

			for (String array : al) {

				int dim = 0;

				String arrayType;

				if (InformationManagers.getInstance().getArrayManager().isUsedAsLookup(array)) {
					bw.append(CodeGenerator.scrub("LOOKUP_t *"));
					arrayType = "LOOKUP_t";
				} else {
					bw.append(CodeGenerator.scrub("double "));
					arrayType = "double";
				}

				NativeArray na = arrays.get(array);
				dim = 0;
				int numDimensions = na.getDimensionNames().length;
				if (arrayType.equals("LOOKUP_t"))
					numDimensions -= 2;
				if (numDimensions <= 0) {
					bw.append(""); // "*"
				} else {
					Integer[] dimSize = new Integer[numDimensions];

					for (String subscript : na.getDimensionNames()) {
						String numInd = Integer.toString(InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dim));
						//		    if (!numInd.equals("0")) {
						bw.append("*");
						dimSize[dim] = Integer.parseInt(numInd);
						//		    } else {
						//			System.out.println("brkpt");
						//		    }

						dim++;
						if (dim == numDimensions)
							break;
					}

					ArrayDeclaration ad = new ArrayDeclaration(CodeGenerator.scrub(legal.get(array)), arrayType, dimSize.length, dimSize);
					arrayDeclarations.add(ad);
				}

				bw.append(CodeGenerator.scrub(legal.get(array)));

				//		na = arrays.get(array);
				//		dim = 0;
				//		for (String subscript : na.getDimensionNames()) {
				//		    String numInd = Integer.toString(ArrayManager.getNumIndicies(array, dim));
				//		    if (!numInd.equals("0")) {
				//			bw.append(CodeGenerator.scrub("["+numInd+"]"));
				//		    }
				//		    
				//		    dim++;
				//		}
				bw.append(";\n");

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public  void generateArrayDeclarationC(BufferedWriter bw/* , String objectName, Translator translator */) {

		try {
			bw.append("void declareArrays() {\n");

			Iterator<ArrayDeclaration> iter = arrayDeclarations.iterator();
			
			// Generate in alphabetic order
			
			List<ArrayDeclaration> sortedAD = new ArrayList<ArrayDeclaration>();
			while (iter.hasNext())
				sortedAD.add(iter.next());
			
			Collections.sort(sortedAD, new Comparator<ArrayDeclaration>() {
				@Override
				public int compare(ArrayDeclaration o1, ArrayDeclaration o2) {
					
					return o1.getName().compareTo(o2.getName());

				}
			});
			
			iter = sortedAD.iterator();
			int alreadyDefined = 0;
			while (iter.hasNext()) {
				ArrayDeclaration ad = iter.next();
				int currentR = 1;


				// NOTE: Currently implemented for only 2 dimensions

				// double x[3];  ==>
				//		double *x;
				//		x = (double *) malloc(3 * sizeof(double));
				// double x[3][4]; ==>
				//		double **x;
				//		x = (double *) malloc(3 * sizeof(double));
				//		int r;
				//		for (r = 0; r < 3; r++) { x[r] = (double *) malloc(4 * sizeof(double)); }
				//
				// bw.append(ad.getName()+" = ("+ad.getType()+"*) malloc("+ad.getNumDimensions()+" * sizeof("+ad.getType()+"*));\n");

				//	        if (ad.getType().equals("LOOKUP_t")) {
				//	            bw.append(ad.getName()+" = ("+ad.getType()+"*) malloc("+ad.getNumDimensions()+" * sizeof("+ad.getType()+"*));\n");
				//	            continue;
				//	        }



				bw.append(ad.getName()+" = ("+ad.getType());
				if (ad.getType().equals("LOOKUP_t"))
					bw.append("*");


				for (int i = 0; i < ad.getNumDimensions(); i++)
					bw.append("*");

				bw.append(") malloc("+ad.getDimensionSize()[0]+" * sizeof("+ad.getType());

				if (ad.getNumDimensions() > 1 || ad.getType().equals("LOOKUP_t"))
					bw.append(" *));\n");
				else
					bw.append("));\n");
				if (ad.getType().equals("LOOKUP_t"))
					continue;
				for (int dim = 1; dim < ad.getNumDimensions(); dim++) {
					if (currentR > alreadyDefined) {
						bw.append("int r"+currentR+";\n");
						alreadyDefined = currentR;
					}
					bw.append("for (r"+currentR+" = 0; r"+currentR+" < "+ad.getDimensionSize()[dim-1]+"; r"+currentR+"++) {\n"); // was dim
					bw.append(ad.getName()+"[r"+currentR+"] = ("+ad.getType()+"*) malloc("+ad.getDimensionSize()[dim]+" * sizeof("+ad.getType()+"));\n");
					currentR++;

				}
				for (int dim = 1; dim < ad.getNumDimensions(); dim++) {
					bw.append("}\n");
				}

			}

			bw.append("}\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String generateMemoryGettersSettersConvenience(String objectName) {
		StringBuffer bw = new StringBuffer();
		List<String> al = new ArrayList<String>();
		al.addAll(scalars);
		Collections.sort(al);
		for (String scalar : al) {
			
			String getter = "get"+StringUtils.capitalize(legal.get(scalar));
			String setter = "set"+StringUtils.capitalize(legal.get(scalar));

			bw.append("public double "+getter+"() {");
			bw.append("return memory."+legal.get(scalar)+";");
			bw.append("}\n");
			
			bw.append("public void   "+setter+"(double value) {");
			bw.append("memory."+legal.get(scalar)+" = value;");
			bw.append("}\n");
		}
		
		return bw.toString();
	}
	
	
	private  void generateMemoryGettersSetters(BufferedWriter bw, String objectName, Translator translator) {
		

		// scalars
		try {
			List<String> al = new ArrayList<String>();
			al.addAll(scalars);
			Collections.sort(al);
			for (String scalar : al) {
				
				String getter = "get"+StringUtils.capitalize(legal.get(scalar));
				String setter = "set"+StringUtils.capitalize(legal.get(scalar));

				bw.append("public double "+getter+"() {");
				bw.append("return "+legal.get(scalar)+";");
				bw.append("}\n");
				
				bw.append("public void   "+setter+"(double value) {");
				bw.append(legal.get(scalar)+" = value;");
				bw.append("}\n");
			}

			// arrays

			// NOTE: we do not want to generate getters for any lookup tables

			al = new ArrayList<String>();
			al.addAll(arrays.keySet());
			Collections.sort(al);
			for (String array : al) {

				if (EquationProcessor.lookups.contains(array)) {
					continue;
				}

				Map<Integer, List<Integer>> indexValueMap = new HashMap<Integer, List<Integer>>();
				for (int dimension = 0; dimension < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dimension++) {
					indexValueMap.put(dimension, new ArrayList<Integer>());
					for (int index = 0; index < InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dimension); index++) {
						indexValueMap.get(dimension).add(index);
					}
				}

				// total number of dimensions in the array
				int numDimensions = InformationManagers.getInstance().getArrayManager().getNumDimensions(array);

				List<StringBuffer> methodName = new ArrayList<StringBuffer>();
				List<StringBuffer> bodySubscript = new ArrayList<StringBuffer>();

				// compute the number of combinations of indicies
				int numToGenerate = 1;
				for (int dimension = 0; dimension < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dimension++) {
					numToGenerate *= indexValueMap.get(dimension).size();
				}
				for (int i = 0; i < numToGenerate; i++) {
					methodName.add(new StringBuffer());
					bodySubscript.add(new StringBuffer());
				}

				// for each dimension compute 
				for (int dimension = 0; dimension < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dimension++) {
					int numPer = 1;
					for (int dim = dimension+1; dim < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dim++) {
						numPer *= indexValueMap.get(dim).size();
					}
					int ptr = 0;

					while (ptr < numToGenerate) {

						for (int subr = 0; subr < indexValueMap.get(dimension).size(); subr++) {
							for (int j = 0; j < numPer; j++) {
								StringBuffer sb = bodySubscript.get(ptr);
								sb.append("["+subr+"]");
								StringBuffer sb2 = methodName.get(ptr);
								ptr++;
								if (sb2.length() == 0) {
									sb2.append("$__$");  // this represents "["
								} else {
									sb2.append("$_$");   // this represents ","
								}
								sb2.append(InformationManagers.getInstance().getArrayManager().getVensimSubscript(array, dimension, subr));
							}
						}
					}

				}




				for (int i = 0; i < bodySubscript.size(); i++) {

					String getter = "get"+StringUtils.capitalize(legal.get(array))+makeLegal(methodName.get(i).toString().trim());
					String original = array;
					String vensim = asVensim(original+makeLegal(methodName.get(i).toString().trim()));
					

					// Start
					int dim = 0;
					NativeArray na = arrays.get(array);
					StringBuffer squareBrackets = new StringBuffer();

					for (String subscript : na.getDimensionNames()) {
						String numInd = Integer.toString(InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dim));
						if (numInd.equals("0"))
							squareBrackets.append("[]");
						dim++;
					}
					// End
					if (squareBrackets.toString().length() > 0)
						continue;
					bw.append("public double"+squareBrackets.toString()+" "+getter+"() {\n");
					bw.append("return memory."+legal.get(array)+bodySubscript.get(i).toString()+";\n");
					bw.append("}\n");
				}




			}
			//		 dsd.addMethodDataSource("ID", objectName+".Memory"+objectName, "getID");
			//			dsd.setSourceType(objectName+".Memory"+objectName);
			
//			bw.append("}\n");
//			bw.close();

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private  void generateDataSetGetters(String objectName, Translator translator) {
		
		int maxGetterCount = 500; // 500
		int getterCount = 0;
		
		
		BufferedWriter bw = null;

		// Create a DataSetDescriptor

		DataSetDescriptor dsd = new DataSetDescriptor("DS1", DataSetDescriptor.DataSetType.AGGREGATE);
		dsd.setIncludeRandomSeed(true);
		dsd.setIncludeTick(true);
		dsd.setScheduleParameters(ScheduleParameters.createRepeating(1, 1));
		
//		dsd.addAggregateMethodDataSource(id, className, methodName, aggType);
//		dsd.addAggregateMethodDataSource("ID", translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName, "getID", AggregateOp.SUM);

//		dsd.addMethodDataSource("ID", translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName, "getID");
//		dsd.setSourceType(translator.getPackageName()+".MemoryGetter"+objectName);
		getterCount++;
		
//		dsd.addMethodDataSource("ID", translator.getPackageName()+".Memory"+objectName, "getID");
//		dsd.setSourceType(translator.getPackageName()+".Memory"+objectName);
		
		String SourceDirectory = translator.getSourceDirectory() + translator.asDirectoryPath(translator.getPackageName())+ "/";
		writeMemoryGetterBaseClass(SourceDirectory, translator.getPackageName(), objectName);
		bw = Translator.openReport(SourceDirectory+"MemoryGetter"+getterFile+"_"+objectName+".java");
		writeMemoryGetterHeader(bw, "MemoryGetter"+getterFile+"_"+objectName, objectName, translator.getPackageName(), getterFile-1);
		
		try {
			bw.append("public String "+"getID"+"() {\n");
			bw.append("return \""+objectName+"\";\n");
			bw.append("}\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// scalars
		try {
			List<String> al = new ArrayList<String>();
			al.addAll(scalars);
			Collections.sort(al);
			for (String scalar : al) {
				
				if (getterCount >= maxGetterCount) {
					bw.append("}\n");
					bw.close();
					getterFile++;
					getterCount = 0;
					bw = Translator.openReport(SourceDirectory+"MemoryGetter"+getterFile+"_"+objectName+".java");
					writeMemoryGetterHeader(bw, "MemoryGetter"+getterFile+"_"+objectName, objectName, translator.getPackageName(), getterFile-1);
				}

				String getter = "get_"+legal.get(scalar);
				String original = scalar;

//				dsd.addMethodDataSource(original, translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName, getter);
				dsd.addAggregateMethodDataSource(original, translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName, getter, AggregateOp.SUM);
//				dsd.setSourceType(translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName);
				
				getterCount++;

				bw.append("public double get_"+legal.get(scalar)+"() {\n");
				bw.append("return memory."+legal.get(scalar)+";\n");
				bw.append("}\n");
				//		bw.append("public  double "+legal.get(scalar)+";\n");
				//		bw.append("List<Double> "+legal.get(scalar)+"_history = new ArrayList<Double>();\n");
			}

			// arrays

			// NOTE: we do not want to generate getters for any lookup tables

			al = new ArrayList<String>();
			al.addAll(arrays.keySet());
			Collections.sort(al);
			for (String array : al) {

				if (EquationProcessor.lookups.contains(array)) {
					continue;
				}

				Map<Integer, List<Integer>> indexValueMap = new HashMap<Integer, List<Integer>>();
				for (int dimension = 0; dimension < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dimension++) {
					indexValueMap.put(dimension, new ArrayList<Integer>());
					for (int index = 0; index < InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dimension); index++) {
						indexValueMap.get(dimension).add(index);
					}
				}

				// total number of dimensions in the array
				int numDimensions = InformationManagers.getInstance().getArrayManager().getNumDimensions(array);

				List<StringBuffer> methodName = new ArrayList<StringBuffer>();
				List<StringBuffer> bodySubscript = new ArrayList<StringBuffer>();

				// compute the number of combinations of indicies
				int numToGenerate = 1;
				for (int dimension = 0; dimension < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dimension++) {
					numToGenerate *= indexValueMap.get(dimension).size();
				}
				for (int i = 0; i < numToGenerate; i++) {
					methodName.add(new StringBuffer());
					bodySubscript.add(new StringBuffer());
				}

				// for each dimension compute 
				for (int dimension = 0; dimension < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dimension++) {
					int numPer = 1;
					for (int dim = dimension+1; dim < InformationManagers.getInstance().getArrayManager().getNumDimensions(array); dim++) {
						numPer *= indexValueMap.get(dim).size();
					}
					int ptr = 0;

					while (ptr < numToGenerate) {

						for (int subr = 0; subr < indexValueMap.get(dimension).size(); subr++) {
							for (int j = 0; j < numPer; j++) {
								StringBuffer sb = bodySubscript.get(ptr);
								sb.append("["+subr+"]");
								StringBuffer sb2 = methodName.get(ptr);
								ptr++;
								if (sb2.length() == 0) {
									sb2.append("$__$");  // this represents "["
								} else {
									sb2.append("$_$");   // this represents ","
								}
								sb2.append(InformationManagers.getInstance().getArrayManager().getVensimSubscript(array, dimension, subr));
							}
						}
					}

				}




				for (int i = 0; i < bodySubscript.size(); i++) {

					String getter = "get_"+legal.get(array)+makeLegal(methodName.get(i).toString().trim());
					String original = array;
					String vensim = asVensim(original+makeLegal(methodName.get(i).toString().trim()));
					
					if (getterCount >= maxGetterCount) {
						bw.append("}\n");
						bw.close();
						getterFile++;
						getterCount = 0;
						bw = Translator.openReport(SourceDirectory+"MemoryGetter"+getterFile+"_"+objectName+".java");
						writeMemoryGetterHeader(bw, "MemoryGetter"+getterFile+"_"+objectName, objectName, translator.getPackageName(),getterFile-1);
					}

//					dsd.addMethodDataSource(vensim, translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName, getter);
					
//					dsd.setSourceType(translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName);
					
					getterCount++;

					// Start
					int dim = 0;
					NativeArray na = arrays.get(array);
					StringBuffer squareBrackets = new StringBuffer();

					for (String subscript : na.getDimensionNames()) {
						String numInd = Integer.toString(InformationManagers.getInstance().getArrayManager().getNumIndicies(array, dim));
						if (numInd.equals("0"))
							squareBrackets.append("[]");
						dim++;
					}
					// End
					if (squareBrackets.toString().length() > 0)
						continue;
					dsd.addAggregateMethodDataSource(vensim, translator.getPackageName()+".MemoryGetter"+getterFile+"_"+objectName, getter, AggregateOp.SUM);
					bw.append("public double"+squareBrackets.toString()+" "+getter+"() {\n");
					bw.append("return memory."+legal.get(array)+bodySubscript.get(i).toString()+";\n");
					bw.append("}\n");
				}




			}
			//		 dsd.addMethodDataSource("ID", objectName+".Memory"+objectName, "getID");
			//			dsd.setSourceType(objectName+".Memory"+objectName);
			
			bw.append("}\n");
			bw.close();

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String ScenarioDirectory = translator.getScenarioDirectory();
		
		dsd.setScheduleParameters(repast.simphony.engine.schedule.ScheduleParameters.createRepeating(1, 1, 
				repast.simphony.engine.schedule.PriorityType.LAST ));

		serialize(dsd, ScenarioDirectory+"repast.simphony.action.data_set_0.xml");
	}
	
	private void writeMemoryGetterBaseClass(String sourceDirectory, String packageName, String objectName) {
		BufferedWriter bw = Translator.openReport(sourceDirectory+"MemoryGetter"+objectName+".java");
		
		try {
			bw.append("package "+packageName+";\n\n");
			bw.append("public class MemoryGetter"+objectName+" {\n");
			bw.append("protected Memory"+objectName+" memory;\n");
			bw.append("public MemoryGetter"+objectName+"(Memory"+objectName+" memory) {\n");
			bw.append("this.memory = memory;\n");
			bw.append("}");
			bw.append("}");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeMemoryGetterHeader(BufferedWriter bw, String newObjectName, String objectName, String packageName, int previous) {

		try {
			bw.append("package "+packageName+";\n\n");
			if (previous == 0) {
				bw.append("public class "+newObjectName+" extends MemoryGetter"+objectName+"{\n");
			} else {
				bw.append("public class "+newObjectName+" extends MemoryGetter"+objectName+"{\n");
//				bw.append("public class "+newObjectName+" extends MemoryGetter"+previous+"_"+objectName+"{\n");
			}

			bw.append("public "+newObjectName+"(Memory"+objectName+ " memory) {\n");
			
			bw.append("super(memory);\n\n");
			bw.append("}\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private  void serialize(DataSetDescriptor dsd, String toFile) {
		XStream xstream = new XStream();
		//	xstream.registerConverter(new FastMethodConverter(xstream));
		try {
			xstream.toXML(dsd, new FileWriter(toFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private  String asVensim(String s) {
		String vensim = s.replace("$__$", "[");
		vensim = vensim.replace("$_$", ",");
		return vensim + "]";
	}

	private  String getInitialTimeVariable() {
		
		Iterator<String> iter = scalars.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			for (String si : SYSTEM_INITIAL) {
				if (si.equalsIgnoreCase(s)) {
					return makeLegal(s);
				}
			}
		}
		
		return "ERROR_INITIAL";
	}

	private  String getFinalTimeVariable() {
		
		Iterator<String> iter = scalars.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			for (String si : SYSTEM_FINAL) {
				if (si.equalsIgnoreCase(s)) {
					return makeLegal(s);
				}
			}
		}
		
		return "ERROR_FINAL";
	}

	private  String getTimeStepVariable() {
		Iterator<String> iter = scalars.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			for (String si : SYSTEM_TIMESTEP) {
				if (si.equalsIgnoreCase(s)) {
					return makeLegal(s);
				}
			}
		}
		
		
		return "ERROR_TIMESTEP";
	}

	public  void dumpLegalNames(BufferedWriter bw) {

		try {
			bw.append("Name,Legal Name\n");
			for (String name : legal.keySet()) {
				bw.append(name+","+legal.get(name)+"\n");
			}

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public  String getDataType(String var) {
		return dataType.get(var);
	}
	
    public String getLegalNameWithSubscripts(Equation equation, String variable) {
	String subscript = "";
	String referenceType = "memory.";
	if (variable.contains("memory."))
	    referenceType = "memory.";
	else if (variable.contains("lookup."))
	    referenceType = "lookup.";
	else if (variable.contains("array."))
	    referenceType = "array.";
	String in = variable.replace("memory.", "").replace("lookup.", "");
	if (ArrayReference.isArrayReference(in)) {
	    in = new ArrayReference(in).getArrayName();
	    subscript = "[" + variable.split("\\[")[1];
	}
	String name = legal.get(in);
	if (name == null) {
	    if (Parser.isInteger(in) || Parser.isReal(in))
		return in;
	    // we haven't seen this yet
	    System.out.println("getLegalNameWithSubscripts Legal -> <"+in+"><"+name+">");
	    addVariable(equation, variable, "double");
	}
	name = legal.get(in);
	if (name == null) {
	    // we haven't seen this yet
	    System.out.println("Legal -> <"+in+"><"+name+">");
	}

	if (Translator.target.equals(ReaderConstants.JAVA)) {
	    return referenceType + name + subscript;
	} else if (Translator.target.equals(ReaderConstants.C)) {
	    return "memory." + name;
	} else {
	    return "memory." + name;
	}
    }
    
    public boolean isScalar(String variable) {
    	return scalars.contains(variable);
    }
    
    public boolean isArray(String variable) {
    	return arrays.containsKey(variable);
    }

	public int getGetterFile() {
		return getterFile;
	}

}
