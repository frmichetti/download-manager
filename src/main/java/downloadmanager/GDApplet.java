/**
*
* @author frmichetti
* <Felipe Rodrigues Michetti at http://portfoliofrmichetti.rhcloud.com/>
*/
package downloadmanager;

import javax.swing.JApplet;




public class GDApplet extends JApplet {

	private static final long serialVersionUID = 4991609249266622796L;

    @Override

    public void init() {



    }



    @Override

    public void start() {

        new GerenciadorDownload();

    }



    @Override

    public void destroy() {

        super.destroy();

    }



    @Override

    public void stop() {

        super.stop(); 

    }


   

}



