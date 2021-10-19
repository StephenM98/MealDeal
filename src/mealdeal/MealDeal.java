//MealDeal.java
//This software wlll store user information in a database and 
//output meals that fit users parameters
package mealdeal;

import java.util.Scanner;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @authors Stephen Miller, Gabriela Lopez, Sean Spurlock
 */

public class MealDeal {

    static Scanner in = new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    //Database url, username, and password
    static String url = "jdbc:mysql://localhost:3306/MealDeal";
    static String username = "java";
    static String password = "password";
    Connection connection;

    public static void main(String[] args) {
        //Finding out if user has account already or not
        System.out.println("Are you a new or existing user? (n/e): ");
        boolean x = false;
        do {
            String userInput = in.nextLine();
            if ("n".equals(userInput) || "N".equals(userInput)) {
                x = true;
                int userID = createProfile();
                mainMenu(userID);
            } else if ("e".equals(userInput) || "E".equals(userInput)) {
                x = true;
                int userID = login();
                mainMenu(userID);
            } else {
                System.out.println("Invalid input. Try again: ");
            }

        } while (x == false);

    }
    //This function will print the main menu and execute functions based on user input
    public static void mainMenu(int userID)
    {
        System.out.println("What would you like to do? \n 1. Create meal\n 2. Update user profile\n 3. Exit");
        boolean x = false;
        do{
            String mainMenuChoice = in.nextLine();
            switch (mainMenuChoice) {
                case "1":
                    createMealPlan(userID);
                    break;
                case "2":
                    changeUserProfile(userID);
                    break;
                case "3":
                    x = true;
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid input");
            }
            System.out.println();
            System.out.println("+----------------------------------------------+");
            System.out.println(" 1. Create meal\n 2. Update user profile\n 3. Exit");
            
        } while(x == false);
    }
    //Function to get user's username if already owning account
    public static int login() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();System.out.println("Please enter your username: ");
            String userName = "'" + in.nextLine() + "'";
            try{ResultSet result = stmt.executeQuery("select UserID from users where Username=" + userName);
            if (result.next()){
                int userID = result.getInt(1);
                return userID;
            }
            else{
                System.out.println("Invalid login");
                System.exit(0);
            }
            } catch (SQLException e)
            {
                System.out.println("Invalid login");
                System.exit(0);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }
        return 1;
    }
    //Function to create profile for new user
    public static int createProfile() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();

            System.out.println("Please enter your preferred username: ");
            String userName = in.nextLine();
            String SQLuserName = "'" + userName + "'";
            System.out.println("Please enter any allergies you have (peanuts, dairy, eggs, wheat, none): ");
            String allergies = "'" + in.nextLine() + "'";
            System.out.println("Please enter what food preference you have (italian, chinese, american): ");
            String preferences = "'" + in.nextLine() + "'";
            System.out.println("Please enter any diets you would like (keto, low-carb, vegetarian, vegan, none): ");
            String diets = "'" + in.nextLine() + "'";
            
            try{
                stmt.executeUpdate("insert users (Username, Allergies, Preferences, Diets) values (" + SQLuserName + ", " + allergies + ", " + preferences + ", " + diets + ")");
                ResultSet result = stmt.executeQuery("select UserID from users where Username=" + SQLuserName);
                if (result.next())
                {
                    int userID = result.getInt(1);
                    return userID;
                }
                } catch (SQLException e) {
                    System.out.println("Invalid Input.");
                }
        }
                  
        catch (SQLException e) {
                throw new IllegalStateException("Cannot connect to the database!", e);
        }   
        return 1;
    }
    //Function to output meal to user
    public static int createMealPlan(int userID) 
    {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();
            //Below query is the sql algorithm to find meal based on user parameters
            ResultSet meal = stmt.executeQuery("select ListOFMeals from food, users "
 + "where users.UserID=" + userID + " and users.Preferences=food.ListOfPreferences and "
+ "(case when users.Allergies != 'none' then users.Allergies != food.ListOfAllergens else users.Allergies='none' end) "
 + "and (case when users.Diets != 'none' then users.Diets=food.ListOfDiets else users.Diets='none' end)");
            
            if (meal.next() == false)
            {
                System.out.println("Unfortunately we have no meals for you, we regularly update our database so keep an eye out!");
                return 1;
            }else{
                System.out.println("Heres some meals we think would be good for you: ");
                do{
                    System.out.println(meal.getString(1));
                } while(meal.next());
            } 
      
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }
        return 1;
    }
    //Function to update user profile with new parameters
    public static void changeUserProfile(int userID) 
    {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();
            String userInput;
            ResultSet rs;
            do {
                System.out.println("+---------------------------------------+");
                System.out.println("What would you like to change? (q to quit): ");
                System.out.println(" 1. Change username\n 2. Change allergies\n 3. Change preferences\n 4. Change diet");
                userInput = in.nextLine();

                switch (userInput){
                    case "1":
                        System.out.println("What would you like your new username to be? ");
                        String newuserName = "'" + in.nextLine() + "'";
                        try{
                            stmt.executeUpdate("UPDATE users SET Username=" + newuserName + " WHERE UserID=" + userID);
                        } catch (SQLException e)
                        {
                            System.out.println("Invalid input.");
                        }    
                        break;
                    case "2":
                        System.out.println("Please enter new allergies (peanut, dairy, eggs, wheat, none): ");
                        String newAllergies = "'" + in.nextLine() + "'";
                        try {
                            stmt.executeUpdate("UPDATE users SET Allergies=" + newAllergies + " WHERE UserID=" + userID);
                        } catch (SQLException e)
                        {
                            System.out.println("Invalid input.");
                        }    
                        break;
                    case "3":
                        System.out.println("Enter new preferences (italian, chinese, american): ");
                        String newPreferences = "'" + in.nextLine() + "'";
                        try{
                            stmt.executeUpdate("UPDATE users SET Preferences=" + newPreferences + " WHERE UserID=" + userID);
                        }catch (SQLException e)
                        {
                            System.out.println("Invalid input.");
                        }    
                        break;
                    case "4":
                        System.out.println("Please enter new diet (keto, low-fat, vegetarian, vegan, none): ");
                        String newDiet = "'" + in.nextLine() + "'";
                        try{
                            stmt.executeUpdate("UPDATE users SET Diets=" + newDiet + " WHERE UserID=" + userID);
                        }catch (SQLException e)
                        {
                            System.out.println("Invalid input.");
                        }
                        break;
                    case "q":
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid input.");
                }
                    
            } while (!"q".equals(userInput));
            
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }
    }

    public MealDeal() {
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(MealDeal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
