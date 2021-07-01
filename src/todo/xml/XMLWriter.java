package todo.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import todo.model.Todo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import static todo.tools.ConstantManager.*;

public class XMLWriter {

    //region Singleton
    private static XMLWriter instance = null;

    private XMLWriter() {
    }

    public static XMLWriter getInstance() {
        if (instance == null) {
            instance = new XMLWriter();
        }
        return instance;
    }
    //endregion

    // Item structure
    //    private int id;            //!! primary key
    //    private String title;
    //    private String task;
    //    private LocalDate deadline;
    //    private Prio priority;
    //    private State state;


//<Root>
//  <Child1 />
//  <Child2 id="1">
//    <Child2a>Text</Child2a>
//  </Child2>
//</Root>

    public boolean writeFile(List<Todo> list) {
        try {
            FileOutputStream out = new FileOutputStream(XML_FILE_PATH_TODOLISTE);

            //root
            Element rootElement = new Element(XML_ELEMENT_ROOT);
            Document doc = new Document(rootElement);

            fillXMLDoc(list, rootElement);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat()); // Formatierung
            outputter.output(doc, out);

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void fillXMLDoc(List<Todo> list, Element rootElement) {
        for (Todo todoItem : list) {

            //children
            Element itemTitle = new Element(XML_ITEM_CONTENT_TITLE);
            itemTitle.setText(todoItem.getTitle());

            Element itemTask = new Element(XML_ITEM_CONTENT_TASK);
            itemTask.setText(todoItem.getTask());

            Element itemDeadline = new Element(XML_ITEM_CONTENT_DEADLINE);
            itemDeadline.setText(todoItem.getDeadline()!=null?todoItem.getDeadline().toString():null);

            Element itemPriority = new Element(XML_ITEM_CONTENT_PRIORITY);
            itemPriority.setText(todoItem.getPriority()!=null?todoItem.getPriority().toString():null);

            Element itemState = new Element(XML_ITEM_CONTENT_STATE);
            itemState.setText(todoItem.getState()!=null?todoItem.getState().toString():null);

            //parent
            Element item = new Element(XML_ELEMENT_ITEM);
            item.setAttribute(XML_ITEM_ATTRIBUTE_ID, "" + todoItem.getId());
            item.addContent(itemTitle);
            item.addContent(itemTask);
            item.addContent(itemDeadline);
            item.addContent(itemPriority);
            item.addContent(itemState);

            rootElement.addContent(item);
        }
    }
}