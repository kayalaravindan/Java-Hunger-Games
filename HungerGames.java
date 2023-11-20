package games;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Maksims Kurjanovics Kravcenko
 * @author Kal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games takes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts (String filename) {

          try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Read the number of districts
            int numOfDistricts = Integer.parseInt(br.readLine().trim());

            // Loop and read the District IDs, creating District objects as we go
            for (int i = 0; i < numOfDistricts; i++) {
                int districtId = Integer.parseInt(br.readLine().trim());
                districts.add(new District(districtId));
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

        // WRITE YOUR CODE HERE

    /**
     * Reads the following from input file (continues to read from the SAME input file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id, effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPeople (String filename) {
            // Reset the file pointer to the start of the file
                try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                    // Skip reading of district information
                    int numOfDistricts = Integer.parseInt(br.readLine().trim());
                    for (int i = 0; i < numOfDistricts; i++) {
                        br.readLine();
                    }
            
                    // Now, read the number of people
                    int numOfPeople = Integer.parseInt(br.readLine().trim());
            
                    for (int i = 0; i < numOfPeople; i++) {
                        String[] personDetails = br.readLine().split(" ");
                        String firstName = personDetails[0];
                        String lastName = personDetails[1];
                        int birthMonth = Integer.parseInt(personDetails[2]);
                        int age = Integer.parseInt(personDetails[3]);
                        int districtId = Integer.parseInt(personDetails[4]);
                        int effectiveness = Integer.parseInt(personDetails[5]);
            
                        Person person = new Person(birthMonth, firstName, lastName, age, districtId, effectiveness);
                       
                        if (age >= 12 && age < 18) {
                            person.setTessera(true);
                        }
                        
                        // iterate over the ArrayList and find the district with the matching ID
                        District targetDistrict = null;
                        for (District d : districts) {
                            if (d.getDistrictID() == districtId) {
                                targetDistrict = d;
                                break;
                            }
                        }
                        
                        if (targetDistrict != null) {
                        if (birthMonth % 2 == 0) {
                            targetDistrict.addEvenPerson(person);
                        } else {
                            targetDistrict.addOddPerson(person);
                        }
                    }
                } 
            }catch (Exception e) {
                    System.out.println("Error processing the input file: " + e.getMessage());
                }
            }
            
    

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    public void addDistrictToGame(TreeNode root, District newDistrict) {

        // WRITE YOUR CODE HERE
            if (root == null) {
                game = new TreeNode(newDistrict, null, null);
                districts.remove(newDistrict); // Remove the district from the districts list upon insertion
                return;
            }
        
            int districtID = newDistrict.getDistrictID();
        
            if (districtID < root.getDistrict().getDistrictID()) {
                if (root.getLeft() == null) {
                    root.setLeft(new TreeNode(newDistrict, null, null));
                    districts.remove(newDistrict); // Remove the district from the districts list
                } else {
                    addDistrictToGame(root.getLeft(), newDistrict);
                }
            } else if (districtID > root.getDistrict().getDistrictID()) {
                if (root.getRight() == null) {
                    root.setRight(new TreeNode(newDistrict, null, null));
                    districts.remove(newDistrict); // Remove the district from the districts list
                } else {
                    addDistrictToGame(root.getRight(), newDistrict);
                }
            }
            // If districtID is already in the tree, we do nothing (since district IDs are unique)
        }
        

    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    public District findDistrict(int id) {

        // WRITE YOUR CODE HERE

    return findDistrict(game, id);
}
// Recursive helper method
private District findDistrict(TreeNode root, int id) {
    if (root == null) {
        return null; // District not found
    }

    int currentID = root.getDistrict().getDistrictID();

    if (id == currentID) {
        return root.getDistrict(); // District found
    } else if (id < currentID) {
        // Search in the left subtree
        return findDistrict(root.getLeft(), id);
    } else {
        // Search in the right subtree
        return findDistrict(root.getRight(), id);
    }
}

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * exist.
     * - If you can't find a person, use StdRandom.uniform(x) where x is the respective 
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {
        return selectDuelersFromGameTree(game);
    }
    
    private DuelPair selectDuelersFromGameTree(TreeNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Not enough candidates to form a DuelPair");
        }
    
        District district = node.getDistrict();
    
        // Ensure that the district has both odd and even populations
        if (!district.getOddPopulation().isEmpty() && !district.getEvenPopulation().isEmpty()) {
            List<Person> oddPopulation = new ArrayList<>(district.getOddPopulation());
            List<Person> evenPopulation = new ArrayList<>(district.getEvenPopulation());
    
            Person person1 = null;
            Person person2 = null;
    
            // Select a child with Tessera from the odd population if available
            for (Person person : oddPopulation) {
                if (person.getTessera() && person.getAge() >= 12 && person.getAge() < 18) {
                    person1 = person;
                    oddPopulation.remove(person);
                    break;
                }
            }
    
            // Select a child with Tessera from the even population if available
            for (Person person : evenPopulation) {
                if (person.getTessera() && person.getAge() >= 12 && person.getAge() < 18) {
                    person2 = person;
                    evenPopulation.remove(person);
                    break;
                }
            }
    
            // If no eligible child is found in the odd population, select a random person
            if (person1 == null && !oddPopulation.isEmpty()) {
                int randomIndex = StdRandom.uniform(oddPopulation.size());
                person1 = oddPopulation.get(randomIndex);
            }
    
            // If no eligible child is found in the even population, select a random person
            if (person2 == null && !evenPopulation.isEmpty()) {
                int randomIndex = StdRandom.uniform(evenPopulation.size());
                person2 = evenPopulation.get(randomIndex);
            }
    
            if (person1 != null && person2 != null) {
                return new DuelPair(person1, person2);
            }
        }
    
        // Recursively try to find duelers in the left and right subtrees
        DuelPair leftPair = selectDuelersFromGameTree(node.getLeft());
        DuelPair rightPair = selectDuelersFromGameTree(node.getRight());
    
        // If duelers are found in either subtree, return the pair
        if (leftPair != null) {
            return leftPair;
        } else if (rightPair != null) {
            return rightPair;
        }
    
        // If no duelers are found, throw an exception
        throw new IllegalArgumentException("Not enough candidates to form a DuelPair");
    }
          
                        
    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not exist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {

        // WRITE YOUR CODE HERE
        game = deleteDistrict(game, id);
    }
    
    private TreeNode deleteDistrict(TreeNode node, int districtID) {
        if (node == null) {
            return node; // District not found
        }
    
        int currentID = node.getDistrict().getDistrictID();
    
        if (districtID < currentID) {
            // Recursively search in the left subtree
            node.setLeft(deleteDistrict(node.getLeft(), districtID));
        } else if (districtID > currentID) {
            // Recursively search in the right subtree
            node.setRight(deleteDistrict(node.getRight(), districtID));
        } else {
            // District with districtID found, this is the node to delete
            if (node.getLeft() == null) {
                return node.getRight(); // Node with only right child or no children
            } else if (node.getRight() == null) {
                return node.getLeft(); // Node with only left child
            }
    
            // Node with two children, find the inorder successor (minimum in right subtree)
            TreeNode inorderSuccessor = findMin(node.getRight());
    
            // Replace the current node with the inorder successor
            node.setDistrict(inorderSuccessor.getDistrict());
    
            // Delete the inorder successor from the right subtree
            node.setRight(deleteDistrict(node.getRight(), inorderSuccessor.getDistrict().getDistrictID()));
        }
    
        return node;
    }
    
    private TreeNode findMin(TreeNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

        
    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {

        // WRITE YOUR CODE HERE
        if (pair.getPerson1() != null && pair.getPerson2() != null) {
            Person person1 = pair.getPerson1();
            Person person2 = pair.getPerson2();
            
            // Have the duelers fight against each other
            Person winner = person1.duel(person2);
            
            // Return the winner back to their district
            District winnerDistrict = findDistrict(winner.getDistrictID());
            if (winner.getBirthMonth() % 2 == 0) {
                winnerDistrict.addEvenPerson(winner);
            } else {
                winnerDistrict.addOddPerson(winner);
            }
            
            // Check the district's population size
            if (winnerDistrict.getOddPopulation().size() == 0 && winnerDistrict.getEvenPopulation().size() == 0) {
                // If both odd and even populations are empty, eliminate the district
                eliminateDistrict(winnerDistrict.getDistrictID());
            }
        } else {
            // Incomplete pair, return the persons back to their respective population
            if (pair.getPerson1() != null) {
                Person person = pair.getPerson1();
                District personDistrict = findDistrict(person.getDistrictID());
                if (person.getBirthMonth() % 2 == 0) {
                    personDistrict.addEvenPerson(person);
                } else {
                    personDistrict.addOddPerson(person);
                }
            }
            if (pair.getPerson2() != null) {
                Person person = pair.getPerson2();
                District personDistrict = findDistrict(person.getDistrictID());
                if (person.getBirthMonth() % 2 == 0) {
                    personDistrict.addEvenPerson(person);
                } else {
                    personDistrict.addOddPerson(person);
                }
            }
        }
    }


    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
