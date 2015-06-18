package de.mih.pdtest.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.mih.pdtest.model.Person;

@RunWith(Arquillian.class)
public class PersonEndpointTest {

	private static final String NAME1 = "Testname1";

	private static final String NAME2 = "Testname2";

	@ArquillianResource
	private URL webappUrl;

	@Deployment(testable = false)
	public static WebArchive createDeployment() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addPackage(de.mih.pdtest.model.Person.class.getPackage())
				.addPackage(
						de.mih.pdtest.rest.RestApplication.class.getPackage())
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml");

	}

	@Test
	public void lifecycleTest() throws MalformedURLException {

		Response r = null;

		Person p = new Person();
		p.setName(NAME1);

		System.out.println("create");
		r = ClientBuilder
				.newClient()
				.target(URI.create(new URL(webappUrl, "api/people")
						.toExternalForm())).request().post(Entity.json(p));

		Assert.assertTrue(r.getStatus() == Response.Status.CREATED
				.getStatusCode());

		String location = r.getHeaderString("Location");
		System.out.println(location);

		Assert.assertTrue(location != null && location.trim().length() > 0);

		System.out.println("read");
		Person p2 = ClientBuilder.newClient().target(location)
				.request("application/json").get(Person.class);

		System.out.println(p2);

		Assert.assertNotNull(p2);
		Assert.assertTrue(p.getName().compareTo(p2.getName()) == 0
				&& p2.getId() > 0);

		p2.setName(NAME2);

		System.out.println("update");
		r = ClientBuilder.newClient().target(location).request()
				.put(Entity.json(p2));

		Assert.assertTrue(r.getStatus() == Response.Status.NO_CONTENT
				.getStatusCode());

		System.out.println("delete");
		r = ClientBuilder.newClient().target(location).request().delete();

		Assert.assertTrue(r.getStatus() == Response.Status.NO_CONTENT
				.getStatusCode());

		System.out.println("finished");
	}
}
