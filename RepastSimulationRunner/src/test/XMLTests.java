package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.jdom2.output.XMLOutputter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simulationsystems.csf.common.internal.messaging.MessagingUtilities;
import org.simulationsystems.csf.common.internal.messaging.xml.XMLUtilities;


public class XMLTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			Document doc = MessagingUtilities.createCachedMessageExchangeTemplate();
			Filter<Element> filter = new org.jdom2.filter.ElementFilter();
			
			@SuppressWarnings("unchecked")
			List<Element> xPathSearchedNodes = (List<Element>) XMLUtilities.executeXPath(doc, "/x:CsfMessageExchange/x:SendingEntity/x:Name","http://www.simulationsystems.org/csf/schemas/CsfMessageExchange/0.1",filter);
			System.out.println(xPathSearchedNodes.get(0).getValue());
			
			xPathSearchedNodes.get(0).setText("new");
			System.out.println("New Document: " + new XMLOutputter().outputString(doc));
		} catch (JDOMException e) {
			
		} catch (IOException e) {
			
		}
	}

}
