package org.simulationsystems.csf.distsys.adapters.api.jade;

import org.jdom2.Document;
import org.simulationsystems.csf.distsys.api.DistSysRunGroupContext;

public class JADE_MAS_RunGroupContext {

	private DistSysRunGroupContext distSysRunGroupContext;
	
	public DistSysRunGroupContext getDistSysRunGroupContext() {
		return distSysRunGroupContext;
	}
	public JADE_MAS_RunGroupContext(DistSysRunGroupContext distSysRunGroupContext) {
		this.distSysRunGroupContext = distSysRunGroupContext;
	}
	
	public Document getCachedMessageExchangeTemplate() {
		return this.distSysRunGroupContext.getCachedMessageExchangeTemplate();
	}

}
