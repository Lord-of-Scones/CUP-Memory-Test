import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory
{
    static int[] MemoryLocation = new int[2000];

    public static void main(String args[])
    {
        String filename = null;

        try
        {
            filename = args[0];
            Scanner CPUin = new Scanner(System.in);
            String cpuRequest = null;
            String [] splitRequest;
            MemoryInsert(filename);

            while(true)
            {

                cpuRequest = CPUin.nextLine();
                splitRequest = cpuRequest.split(",");

                //Read (r), splitRequest[1] is the address value
                if(splitRequest[0].equals("r"))
                {
                    System.out.println(MemoryLocation[Integer.parseInt(splitRequest[1])]); //Sends the value stored in Memory at address specified by CPU
                }
                //Write (w), splitRequest[1] is the address value, splitRequest[2] is the data to be written to memory
                else if(splitRequest[0].equals("w"))
                {
                    //Overwrites the data in a location in memory provided by the CPU with data provided by the CPU
                    MemoryLocation[Integer.parseInt(splitRequest[1])] = Integer.parseInt(splitRequest[2]);
                }
                //End (ed)
                else if(splitRequest[0].equals("ed"))
                {
                    CPUin.close();
                    System.exit(0);
                }
            }


        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            System.out.println("Error Argument not present please enter valid filename");
            System.exit(-1);
        }
    }

    public static void MemoryInsert(String filename)
    {

        //Opens file of passed in filename
        File openedFile = new File(filename);

        try
        {
            Scanner scanIn = new Scanner(openedFile);

            int currentAddress = 0;

            while(scanIn.hasNext())
            {
                String line = scanIn.nextLine(); // Gets the next line in the inputted file


                String[] instruction = line.split(" "); // Parses line into a string array
                instruction[0] = instruction[0].trim();// Trims away all excess data, comments, or whitespace in the string array leaving only first value

                if(!instruction[0].equals("//") && !instruction[0].equals(""))
                {

                    //Checks if value is a .#### format address
                    if(".".equals(instruction[0].substring(0,1)))
                    {

                        //parses the rest of the data after the '.' into an integer
                        currentAddress = Integer.parseInt(instruction[0].substring(1));
                    }
                    else
                    {

                        MemoryLocation[currentAddress] = Integer.parseInt(instruction[0]); //Adds the instruction to the memory at the current address
                        currentAddress++; //Adds one to the address moving on to the next address
                    }
                }



            }

            //Closes Scanner
            scanIn.close();

        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Error File could not be opened");
        }

    }


}

