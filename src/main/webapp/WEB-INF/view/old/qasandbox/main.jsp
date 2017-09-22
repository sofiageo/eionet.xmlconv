<%@include file="/WEB-INF/view/old/taglibs.jsp" %>
<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/vue/2.4.4/vue.min.js"/>--%>
<%--<script src="https://cdn.jsdelivr.net/npm/vue"></script>--%>

<spring:message code="label.qasandbox.extractSchema" var="extractSchemaLabel"/>
<spring:message code="label.qasandbox.searchXML" var="searchXmlLabel"/>
<spring:message code="label.qasandbox.findScripts" var="searchScriptsLabel"/>

<c:set var="permissions" scope="page" value="${sessionScope['qascript.permissions']}"/>
<c:set var="scriptlangs" scope="page" value="${sessionScope['qascript.scriptLangs']}"/>

<div id="qasandbox">
  <%--<p>{{ message }}</p>--%>
  <h2>QA Sandbox</h2>
  <h4>XML Schema</h4>
  <button>Test</button>
  <button>Test2</button>
  <h4>Upload XML file or select XML file URL</h4>
  <c:if test="${permissions.qsuPrm}">
    <c:if test="${not(fn:contains(header['User-Agent'],'MSIE 9.0'))}">
      <button style="float:right;" id="clickable">Upload file</button>
      <form action="/qasandbox/upload" id="my-dropzone" class="dropzone">
        <ul id="dropzone-previews" class="dropzone-previews"></ul>
      </form>
      <script type="text/javascript" src="<c:url value="/js/dropzone.min.js"/>"></script>

      <script id="mypreview" type="text/template">
        <li class="dz-preview dz-file-preview">
          <div class="dz-details">
            <div class="dz-filename">
              <span data-dz-name></span>
              <span>(<span data-dz-size></span>)</span>
              <div style="float:right">
                <button class="dz-remove-button" style="margin-left:5px" type="button" data-dz-remove>Remove</button>
                <button class="dz-select-button" style="margin-left:5px" type="button">Select</button>
              </div>
            </div>
            <div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>
          </div>
          <div class="dz-success-mark"><span>✔</span></div>
          <div class="dz-error-mark"><span>✘</span></div>
          <div class="dz-error-message"><span data-dz-errormessage></span></div>
        </li>
      </script>

      <script type="text/javascript">
        $.ajaxSetup({cache: false});
        $(document).on('click', '.dz-filename span', function (event) {
          for (var index = 0; index < Dropzone.forElement("#my-dropzone").files.length; index++) {
            if (Dropzone.forElement("#my-dropzone").files[index].name == $(this).text()) {
              $("#txtSourceUrl").val(Dropzone.forElement("#my-dropzone").files[index].url)
            }
          }
        });
        $(document).on('click', '.dz-select-button', function (event) {
          for (var index = 0; index < Dropzone.forElement("#my-dropzone").files.length; index++) {
            if (Dropzone.forElement("#my-dropzone").files[index].name == $(this).parent().parent().children(':first').text()) {
              $("#txtSourceUrl").val(Dropzone.forElement("#my-dropzone").files[index].url)
            }
          }
        });
        var ctx = '${pageContext.request.contextPath}';
        Dropzone.options.myDropzone = {
          dictDefaultMessage: "",
          url: ctx + "/qasandbox/upload",
          clickable: "#clickable",
          acceptedFiles: ".xml, .gml",
          maxFiles: "5",
          maxFilesize: "300",
          createImageThumbnails: "false",
          addRemoveLinks: "false",
          previewsContainer: "#dropzone-previews",
          previewTemplate: document.getElementById("mypreview").innerHTML,
          init: function () {
            $.getJSON(ctx + "/qasandbox/action?command=getFiles", function (data) {
              if (data.Data.length != 0) {
                $.each(data.Data, function (index, val) {
                  var mockFile = {name: val.name, size: val.size, url: val.url};
                  Dropzone.forElement("#my-dropzone").emit("addedfile", mockFile);
                  Dropzone.forElement("#my-dropzone").emit("complete", mockFile);
                  Dropzone.forElement("#my-dropzone").files.push(mockFile);
                });
              }
            });
            this.on("success", function (file, responseText) {
              $("#txtSourceUrl").val(responseText.url)
              var mockFile = {name: file.name, size: file.size, url: responseText.url};
              Dropzone.forElement("#my-dropzone").files.push(mockFile);
            });
            this.on("uploadprogress", function (file, progress, bytesSent) {
              //console.log("Progress :" + progress);
              //$('.dz-upload').text(Math.round(progress) + "%")
            });
            this.on("removedfile", function (file) {
              $.get(ctx + "/qasandbox/action", {command: "deleteFile", filename: file.name});
            });
          }
        };
      </script>
    </c:if>
  </c:if>

  <h4>QA Script</h4>
  <div>Script type</div>
  <h4 v-if=>Script content</h4>
  <div>
    <textarea>
    </textarea>
  </div>
  <button>Run script</button>
  <button>Add to workqueue</button>

</div>

<script src="https://unpkg.com/vue"></script>
<script>
  var form = new Vue({
    el: '#qasandbox',
    data: {
      message: "Hello world!"
    }
  });
</script>