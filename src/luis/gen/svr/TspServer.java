package luis.gen.svr;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import luis.gen.EngineListener;
import luis.gen.Population;
import luis.gen.tsp.TspEngine;
import luis.gen.tsp.TspParams;
import luis.gen.tsp.TspSolution;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONWriter;

public class TspServer extends AbstractHandler implements EngineListener {

	private GenInfo ginfo;

	public static void main(String[] args) throws Exception {
		new TspServer().run(args);
	}

	private void run(String[] args) throws Exception {
		Server server = startServer();
		TspParams params = initParams(args);
		TspEngine engine = new TspEngine(params);
		engine.setListener(this);
		ginfo = new GenInfo(engine);
		try {
			engine.run();
		}
		finally {
			server.stop();
		}
	}

	private Server startServer() throws Exception {
		// See http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty
		Server server = new Server(8080);
        ResourceHandler fileHandler = new ResourceHandler();
        fileHandler.setDirectoriesListed(true);
        fileHandler.setWelcomeFiles(new String[]{ "index.html" });
        fileHandler.setResourceBase("./web");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { fileHandler, this, new DefaultHandler() });
        server.setHandler(handlers);
		server.start();
		return server;
	}

	private TspParams initParams(String[] args) {
		//TODO: allow parameter editing through web interface
		TspParams params = new TspParams();
		if (args.length > 0) params.numCities = Integer.parseInt(args[0]);
		else params.numCities = 200;
		params.population = 50;
		params.elite = 10;
		params.invertRatio = 0.2;
		params.weightExponent = 2.0;
		return params;
	}

	@Override
	public void engineStep(Population pop, int generationCount) {
		synchronized (ginfo) {
			ginfo.markTime();
			ginfo.generationCount = generationCount;
			if (ginfo.incumbent != null &&
				pop.getIncumbent().evaluate() < ginfo.incumbent.evaluate()) {
				ginfo.lastIncumbentGen = generationCount;
				ginfo.lastIncumbentWhen = ginfo.getElapsed();
			}
			ginfo.incumbent = pop.getIncumbent();
		}
	}


	//------------------------------ Response handling ------------------------------

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			if ("/status".equals(target)) jsonStatus(baseRequest, response);
			else if ("/cities".equals(target)) jsonCities(baseRequest, response);
			else if ("/info".equals(target)) textInfo(baseRequest, response);
			else baseRequest.setHandled(false);
		} catch (JSONException e) {
			throw new IOException(e);
		}
		//TODO: keep improving the UI.
	}

	private void jsonCities(Request baseRequest, HttpServletResponse response) throws IOException, JSONException {
		baseRequest.setHandled(true);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		synchronized (ginfo) {
			new JSONWriter(out)
				.object()
					.key("cityX").value(new JSONArray(ginfo.engine.getMap().cityX))
					.key("cityY").value(new JSONArray(ginfo.engine.getMap().cityY))
				.endObject();
		}
	}

	private void jsonStatus(Request baseRequest, HttpServletResponse response) throws IOException, JSONException {
		baseRequest.setHandled(true);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		synchronized (ginfo) {
			TspSolution tspIncumbent = (TspSolution) ginfo.incumbent;
			new JSONWriter(out)
				.object()
					.key("status").value("running")
					.key("generation").value(ginfo.generationCount)
					.key("gpm").value(ginfo.getGpm())
					.key("eval").value(ginfo.incumbent.evaluate())
					.key("cities").value(new JSONArray(tspIncumbent.getCities()))
					.key("lastIncumbentGen").value(ginfo.lastIncumbentGen)
					.key("lastIncumbentWhen").value(ginfo.lastIncumbentWhen)
					.key("elapsed").value(ginfo.getElapsed())
				.endObject();
		}
	}
	
	private void textInfo(Request baseRequest, HttpServletResponse response) throws IOException {
		baseRequest.setHandled(true);
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>GenTsp</title></head><body>");
		synchronized (ginfo) {
			out.println("Generation: " + ginfo.generationCount + " - "
					+ ginfo.getGpm() + " GPM<br />");
			out.println("Incumbent: " + ginfo.incumbent);
		}
		out.println("</body></html>");
	}

}