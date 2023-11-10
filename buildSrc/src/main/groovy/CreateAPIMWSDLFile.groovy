/**
 *  Take a WSDL file with imported xsd files that are at the same location as the wsdl (needs to be a flat structure)
 */
import groovy.xml.XmlSlurper;
import groovy.xml.XmlUtil;
import groovy.xml.slurpersupport.GPathResult;
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
class CreateAPIMWSDLFile extends DefaultTask {

  /** The WSDL location */
  @InputFile
  File wsdlFile = new File("src/main/resources/ws/dartsService/dartsService.wsdl")

  /** The WSDL url to use*/
  @Input
  String urlToReplace = "http://test"

  /** The output file where the compatible APIM file will be spat out*/
  @OutputFile
  File outputFile = new File("src/main/resources/ws/dartsService.wsdl")

  @TaskAction
  void join() {
    File f =  wsdlFile;
    XmlSlurper parser = createSlurper();
    def doc = parser.parse(new StringReader(f.getText()));
    def node = process(doc);

    // serialize the xml to a string
    def xmlOutput = XmlUtil.serialize(node);
    outputFile.write(xmlOutput);
  }

  // setup the slarper with the relevant feature enabled that preserves xml namespaces
  static XmlSlurper createSlurper()
  {
    XmlSlurper parser = new XmlSlurper(false, true);
    parser.setFeature("http://xml.org/sax/features/validation", false)
    parser.setFeature('http://xml.org/sax/features/namespaces', true)
    parser.setFeature('http://xml.org/sax/features/namespace-prefixes', true)
    return parser;
  }

  /**
   * sets up some name spaces so we can search for name space targetted items
   */
  static void setupWithNamespace(var xsd)
  {
    xsd.declareNamespace(
      xsd: 'http://www.w3.org/2001/XMLSchema');

    xsd.declareNamespace(
      wsdl: 'http://schemas.xmlsoap.org/wsdl/');

    xsd.declareNamespace(
      soap: 'http://schemas.xmlsoap.org/wsdl/soap/');
  }

  GPathResult process(GPathResult xsd)
  {
    setupWithNamespace(xsd);

    def types = xsd.'wsdl:types';

    // we we a wsdl structure and do we have a types block
    if (types.size() > 0) {

      // replace the placeholder with the specified url
      replaceWithUrl(xsd)

      var schemas = types.'xsd:schema';
      var schemaToAdd = new ArrayList()
      for (def schema : schemas) {
        var xsdFile = schema.'xsd:import'.@schemaLocation;

        if (xsdFile.size() > 0) {

          // if we have an import file referenced then recurse and process the xsd
          var doc = createSlurper().parse(new File(wsdlFile.getParent().toString() + "/" + xsdFile[0]));
          doc = process(doc)
          schemaToAdd.add(doc)
        }

        // remove all processed files
        schema.replaceNode({})
      }

      // now add the new schema content to the wsdl
      for (def schema : schemaToAdd) {
        types.appendNode(schema)
      }
    }
    else
    {
      // remove any imports as these are assumed to be referenced from the top level wsdl
      def xsdImports = xsd.'xsd:import';
      for (def xsdImport : xsdImports) {
        xsdImport.replaceNode({ })
      }

      // We remove any name spaces around an individual element as this was causing problems. May need to
      // revisit this
      removeElementNamespace(xsd)
    }

    return xsd;
  }

  /**
   * replace the placeholder string with the url
   */
  void replaceWithUrl(var doc)
  {
    var node = doc.'wsdl:service'.'wsdl:port'.'soap:address';
    var locationAttribute = node.@location
    if (locationAttribute.equals('REPLACE_WITH_ACTUAL_URL'))
    {
      // set the location attribute
      node.@location = urlToReplace
    }
  }

  static void removeElementNamespace(var doc)
  {
    // clear down any namespace around an element
    doc.'**'.findAll { node ->
      {
        if (node.name().endsWith("element")) {
          var key;

          node.attributes().find {
            if (it.key!=null && it.key.toString().startsWith("xmlns")) {
              key = it
            }
          }

          if (key!=null) {
            var name = node.attributes().name

            // clear all elements from the node
            node.attributes().removeAll{return true}

            // reapply the name to the element as this is mandatory
            node.attributes().put("name", name)
          }
        }
      }
    }
  }
}
