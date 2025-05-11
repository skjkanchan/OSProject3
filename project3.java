
public class Project3 {
    public static void main(String[] args) {

        //get the command
        String command = args[0]; 
        
        if (command.equals("create")) {
            System.out.println("In create");

        } else if (command.equals("insert")) {
            System.out.println("In insert");

        } else if (command.equals("search")) {
            System.out.println("In search");

        } else if (command.equals("load")) {
            System.out.println("In load");

        } else if (command.equals("print")) {
            System.out.println("In print");
            
        } else if (command.equals("extract")) {
            System.out.println("In extract");
            
        }
    }

}