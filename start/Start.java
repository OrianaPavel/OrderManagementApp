package start;

import presentation.Controller;
import presentation.View;

/**
 * Clasa care contine metoda main. Initializeaza GUI
 */
public class Start {
	public static void main(String[] args){
		View view = new View();
		Controller controller = new Controller(view);

	}

}
