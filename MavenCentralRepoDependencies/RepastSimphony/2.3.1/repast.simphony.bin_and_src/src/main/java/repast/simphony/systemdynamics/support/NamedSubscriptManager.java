/**
 * 
 */
package repast.simphony.systemdynamics.support;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bragen
 *
 */
public class NamedSubscriptManager {

	private  Map<String, NamedSubscriptDefinition> namedSubscripts;

	public NamedSubscriptManager() {
		namedSubscripts = new HashMap<String, NamedSubscriptDefinition>();
	}

	public  void subscriptDefinition(String subscriptName, String...subscriptNames) {

		// Is this the first time we have seen arrays?
		if (!namedSubscripts.containsKey(subscriptName)) {
			namedSubscripts.put(subscriptName, new NamedSubscriptDefinition(subscriptName, subscriptNames));
		}
	}

	public  void subscriptDefinition(String subscriptName, List<String> subscriptNames) {

		// Is this the first time we have seen arrays?
		if (!namedSubscripts.containsKey(subscriptName)) {
			namedSubscripts.put(subscriptName, new NamedSubscriptDefinition(subscriptName, subscriptNames));
		}
	}

	public  List<String> getValuesFor(String subscript) {
		List<String> al = new ArrayList<String>();
		if (!isNamedSubscript(subscript.replace("!", ""))) {
			al.add(subscript);
		} else {
			for (String val : namedSubscripts.get(subscript.replace("!", "")).getValues()) {
				al.addAll(getValuesFor(val));
			}
		}
		return al;
	}

	public  int getNumIndexFor(String subscript) {
		return getValuesFor(subscript.replace("!", "")).size();
	}

	public  int getIndexFor(String namedSubscript, String subscript) {
		return getValuesFor(namedSubscript).indexOf(subscript);
	}

	public  boolean isNamedSubscript(String subscript) {
		if (namedSubscripts.containsKey(subscript.replace("!", "")))
			return true;
		else
			return false;
	}

	public  boolean hasNamedSubscript(ArrayReference arrayReference) {
		for (String subscript : arrayReference.getSubscripts()) {
			if (namedSubscripts.containsKey(subscript))
				return true;
		}
		return false;

	}

	public  String[] getExpandedSubscripts(ArrayReference arrayReference) {
		List<String> exp = new ArrayList<String>();
		for (String subscript : arrayReference.getSubscripts()) {
			if (namedSubscripts.containsKey(subscript))
				exp.addAll(getValuesFor(subscript));
		}
		int i = 0;
		String[] expSub = new String[exp.size()];
		for (String s : exp) {
			expSub[i++] = s;
		}

		return expSub;

	}

	public  void dumpMappings(BufferedWriter bw) {

		try {
			bw.append("Name,Value,Index\n");
			for (String name : namedSubscripts.keySet()) {
				NamedSubscriptDefinition nsd = namedSubscripts.get(name);
				int index = 0;
				for (String value : nsd.getValues()) {
					bw.append(name+","+value+","+(index++)+"\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public  int getIndex(String name, String value) {
		if (namedSubscripts.containsKey(name)) {
			return namedSubscripts.get(name).getValues().indexOf(value);
		} else {
			return -1;
		}
	}
}
