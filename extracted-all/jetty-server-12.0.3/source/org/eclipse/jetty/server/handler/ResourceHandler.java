package org.eclipse.jetty.server.handler;

import java.time.Duration;
import java.util.List;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.content.FileMappingHttpContentFactory;
import org.eclipse.jetty.http.content.HttpContent;
import org.eclipse.jetty.http.content.PreCompressedHttpContentFactory;
import org.eclipse.jetty.http.content.ResourceHttpContentFactory;
import org.eclipse.jetty.http.content.ValidatingCachingHttpContentFactory;
import org.eclipse.jetty.http.content.VirtualHttpContentFactory;
import org.eclipse.jetty.http.content.HttpContent.Factory;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool.NonPooling;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;

public class ResourceHandler extends Handler.Wrapper {
   private final ResourceService _resourceService = this.newResourceService();
   private ByteBufferPool _byteBufferPool;
   private Resource _baseResource;
   private Resource _styleSheet;
   private MimeTypes _mimeTypes;
   private List<String> _welcomes = List.of("index.html");

   public ResourceHandler() {
      this(null);
   }

   public ResourceHandler(Handler handler) {
      super(handler);
   }

   protected ResourceService newResourceService() {
      return new ResourceHandler.HandlerResourceService();
   }

   public ResourceService getResourceService() {
      return this._resourceService;
   }

   @Override
   public void doStart() throws Exception {
      Context context = ContextHandler.getCurrentContext(this.getServer());
      if (this._baseResource == null && context != null) {
         this._baseResource = context.getBaseResource();
      }

      this.setMimeTypes(context == null ? MimeTypes.DEFAULTS : context.getMimeTypes());
      this._byteBufferPool = this.getByteBufferPool(context);
      ResourceService resourceService = this.getResourceService();
      resourceService.setHttpContentFactory(this.newHttpContentFactory());
      resourceService.setWelcomeFactory(this.setupWelcomeFactory());
      if (this.getStyleSheet() == null) {
         this.setStyleSheet(this.getServer().getDefaultStyleSheet());
      }

      super.doStart();
   }

   private ByteBufferPool getByteBufferPool(Context context) {
      if (context == null) {
         return new NonPooling();
      } else {
         Server server = this.getServer();
         return (ByteBufferPool)(server == null ? new NonPooling() : server.getByteBufferPool());
      }
   }

   public Factory getHttpContentFactory() {
      return this._resourceService.getHttpContentFactory();
   }

   protected Factory newHttpContentFactory() {
      Factory contentFactory = new ResourceHttpContentFactory(ResourceFactory.of(this.getBaseResource()), this.getMimeTypes());
      contentFactory = new FileMappingHttpContentFactory(contentFactory);
      contentFactory = new VirtualHttpContentFactory(contentFactory, this.getStyleSheet(), "text/css");
      contentFactory = new PreCompressedHttpContentFactory(contentFactory, this.getPrecompressedFormats());
      return new ValidatingCachingHttpContentFactory(contentFactory, Duration.ofSeconds(1L).toMillis(), this.getByteBufferPool());
   }

   protected ResourceService.WelcomeFactory setupWelcomeFactory() {
      return (content, request) -> {
         if (this._welcomes == null) {
            return null;
         } else {
            for (String welcome : this._welcomes) {
               String pathInContext = Request.getPathInContext(request);
               String welcomeInContext = URIUtil.addPaths(pathInContext, welcome);
               Resource welcomePath = content.getResource().resolve(welcome);
               if (Resources.isReadableFile(welcomePath)) {
                  return welcomeInContext;
               }
            }

            return null;
         }
      };
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      if (!HttpMethod.GET.is(request.getMethod()) && !HttpMethod.HEAD.is(request.getMethod())) {
         return super.handle(request, response, callback);
      } else {
         HttpContent content = this._resourceService.getContent(Request.getPathInContext(request), request);
         if (content == null) {
            return super.handle(request, response, callback);
         } else {
            this._resourceService.doGet(request, response, callback, content);
            return true;
         }
      }
   }

   public Resource getBaseResource() {
      return this._baseResource;
   }

   public ByteBufferPool getByteBufferPool() {
      return this._byteBufferPool;
   }

   public String getCacheControl() {
      return this._resourceService.getCacheControl();
   }

   public List<String> getGzipEquivalentFileExtensions() {
      return this._resourceService.getGzipEquivalentFileExtensions();
   }

   public MimeTypes getMimeTypes() {
      return this._mimeTypes;
   }

   public Resource getStyleSheet() {
      return this._styleSheet == null ? this.getServer().getDefaultStyleSheet() : this._styleSheet;
   }

   public List<String> getWelcomeFiles() {
      return this._welcomes;
   }

   public boolean isAcceptRanges() {
      return this._resourceService.isAcceptRanges();
   }

   public boolean isDirAllowed() {
      return this._resourceService.isDirAllowed();
   }

   public boolean isEtags() {
      return this._resourceService.isEtags();
   }

   public List<CompressedContentFormat> getPrecompressedFormats() {
      return this._resourceService.getPrecompressedFormats();
   }

   public ResourceService.WelcomeMode getWelcomeMode() {
      return this._resourceService.getWelcomeMode();
   }

   public void setAcceptRanges(boolean acceptRanges) {
      this._resourceService.setAcceptRanges(acceptRanges);
   }

   public void setBaseResource(Resource base) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._baseResource = base;
      }
   }

   public void setBaseResourceAsString(String base) {
      this.setBaseResource(base == null ? null : ResourceFactory.of(this).newResource(base));
   }

   public void setCacheControl(String cacheControl) {
      this._resourceService.setCacheControl(cacheControl);
   }

   public void setDirAllowed(boolean dirAllowed) {
      this._resourceService.setDirAllowed(dirAllowed);
   }

   public void setEtags(boolean etags) {
      this._resourceService.setEtags(etags);
   }

   public void setGzipEquivalentFileExtensions(List<String> gzipEquivalentFileExtensions) {
      this._resourceService.setGzipEquivalentFileExtensions(gzipEquivalentFileExtensions);
   }

   public void setPrecompressedFormats(CompressedContentFormat... precompressedFormats) {
      this.setPrecompressedFormats(List.of(precompressedFormats));
   }

   public void setPrecompressedFormats(List<CompressedContentFormat> precompressedFormats) {
      this._resourceService.setPrecompressedFormats(precompressedFormats);
   }

   public void setEncodingCacheSize(int encodingCacheSize) {
      this._resourceService.setEncodingCacheSize(encodingCacheSize);
   }

   public int getEncodingCacheSize() {
      return this._resourceService.getEncodingCacheSize();
   }

   public void setMimeTypes(MimeTypes mimeTypes) {
      this._mimeTypes = mimeTypes;
   }

   public void setWelcomeMode(ResourceService.WelcomeMode welcomeMode) {
      this._resourceService.setWelcomeMode(welcomeMode);
   }

   public void setStyleSheet(Resource styleSheet) {
      this._styleSheet = styleSheet;
   }

   public void setWelcomeFiles(String... welcomeFiles) {
      this.setWelcomeFiles(List.of(welcomeFiles));
   }

   public void setWelcomeFiles(List<String> welcomeFiles) {
      this._welcomes = welcomeFiles;
   }

   private class HandlerResourceService extends ResourceService {
      @Override
      protected void rehandleWelcome(Request request, Response response, Callback callback, String welcomeTarget) throws Exception {
         HttpURI newHttpURI = HttpURI.build(request.getHttpURI()).pathQuery(welcomeTarget);
         Request newRequest = Request.serveAs(request, newHttpURI);
         if (!ResourceHandler.this.getServer().handle(newRequest, response, callback)) {
            super.rehandleWelcome(request, response, callback, welcomeTarget);
         }
      }
   }

   public static class ResourceContext extends ContextHandler {
      public ResourceContext() {
         this.setHandler(new ResourceHandler());
      }
   }
}
