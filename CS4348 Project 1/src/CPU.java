import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.lang.Runtime;
import java.util.Scanner;


public class CPU {

    //Initializing registers
    static int PC = 0;
    static int SP = 1000;
    static int IR = 0;
    static int AC = 0;
    static int X = 0;
    static int Y = 0;
    static Scanner scanIn;
    static PrintWriter pwOut;
    //Used to determine current mode (either user or kernel) Initialized to false for user mode at start
    static boolean current_Mode = false;

    public static void main(String args[])
    {

        //Exception handling try block
        try
        {
            String filename = "";
            int pTimer = 0;
            int currentTimer = 0;
            int x = 0;



            //Reads in arguments passed by user into variables
            filename = args[0]; //TODO if the argument isnt given to you, create catch for exception (Array index out of bounds)

            pTimer = Integer.parseInt(args[1]);


            //creates a runtime and creates a process that runs the memory.java file and reads the user input file into the memory
            Runtime runner = Runtime.getRuntime();
            Process proc = runner.exec("java Memory " + filename);

            //Initializing printwriter and scanner variables with created process
            pwOut = new PrintWriter(proc.getOutputStream());
            scanIn = new Scanner(proc.getInputStream());

            do
            {
                memorySend("r," + PC);
                memoryRead();
                currentTimer++;

                //System.out.println("\ntimer " + currentTimer);
                //System.out.println("\nInstruction number " + IR);

                //System.out.println("PC Value:" + PC);
                switch(IR)
                {
                    case 1: //Loads the value after instruction into AC (Load value)
                    {
                        PC += 1;
                        memorySend("r," + PC);
                        memoryRead();
                        AC = IR;
                        PC++;
                        break;
                    }
                    case 2: // Load the value at the given address into AC (Load addr)
                    {
                        PC += 1;
                        //Reads address from memory that we need to pull value from
                        memorySend("r," + PC);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker(IR);

                        //Reads value at address that we read above and sets AC equal to that value
                        memorySend("r," + IR);
                        memoryRead();
                        AC = IR;
                        PC++;
                        break;
                    }
                    case 3:  //Load the value from the address found in the given address into the AC (LoadInd addr)
                    {
                        PC += 1;
                        //Reads address from memory that we need to pull value from
                        memorySend("r," + PC);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker(IR);
                        //Reads value at address that we read above and sets AC equal to that value
                        memorySend("r," + IR);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker(IR);
                        //Reads value at address equal to the value found above
                        memorySend("r," + IR);
                        memoryRead();

                        AC = IR;
                        PC++;
                        break;
                    }
                    case 4:  //Loads the value located at address equal to provided number plus X into AC (LoadIdxX addr)
                    {
                        PC += 1;
                        //Reads address from memory that we need to pull value from
                        memorySend("r," + PC);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker((IR+X));

                        //Reads value into IR found at address equal to value found above plus X
                        memorySend("r," + (IR + X));
                        memoryRead();

                        AC = IR; //Loads value into AC

                        PC++;
                        break;
                    }
                    case 5:  //Loads the value located at address equal to provided number plus Y into AC (LoadIdxY addr)
                    {
                        PC += 1;
                        //Reads address from memory that we need to pull value from
                        memorySend("r," + PC);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker((IR+Y));

                        //Reads value into IR found at address equal to value found above plus Y
                        memorySend("r," + (IR + Y));
                        memoryRead();

                        AC = IR; //Loads value into AC

                        PC++;
                        break;
                    }
                    case 6:  //Loads value from address equal to (SP + X) into AC (LoadSpX)
                    {

                        //Checks to make sure memory access permissions are correct
                        permissionChecker((SP + X));

                        //Reads in value found at address equal to (SP + X) into IR register from Memory
                        memorySend("r," + (SP + X));
                        memoryRead();

                        AC = IR; //Loads value into AC

                        PC++;
                        break;
                    }
                    case 7:  //Store the current AC value into address given (Store addr)
                    {
                        PC += 1;

                        //Reads address from memory that we need to pull value from
                        memorySend("r," + PC);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker(IR);

                        memorySend("w," + IR + "," + AC); // Tells memory to overwrite value at address 'IR' with value in AC

                        PC++;
                        break;
                    }
                    case 8:  //Gets a random integer from 1 to 100 and puts it into the AC (Get)
                    {
                        //Creates a random number generator and generates a random number between 1 and 100
                        Random r = new Random();
                        int r_num = 1 + r.nextInt(100);

                        // Set AC equal to the random number that was generated
                        AC = r_num;

                        PC++;
                        break;
                    }
                    case 9:  // If port = 1 then AC is output on the screen as an int, if = 2 then writes AC to screen as a char (Put Port)
                    {
                        PC += 1;

                        //Finds value of port provided by program in memory and stores it in IR
                        memorySend("r," + PC);
                        memoryRead();

                        // If/elseif statement that prints AC to screen as either a number or char based off of port value in IR
                        if(IR == 1)
                        {
                            System.out.print(AC);
                        }
                        else if(IR == 2)
                        {
                            char convertedAC = (char)AC;
                            System.out.print(convertedAC);
                        }

                        PC++;
                        break;
                    }
                    case 10:  //Adds the value stored in X to the AC (AddX)
                    {
                        AC += X; // Adds x to the value in AC

                        PC++;
                        break;
                    }
                    case 11: //Adds the value stored in Y to the AC (AddY)
                    {
                        AC += Y; // Adds Y to the value in AC

                        PC++;
                        break;
                    }
                    case 12:  //Subtracts the value stored in X from the AC (SubX)
                    {
                        AC -= X; // subtracts the value of X from the value in AC

                        PC++;
                        break;
                    }
                    case 13:  //Subtracts the value stored in Y from the AC (SubY)
                    {
                        AC -= Y; // subtracts the value of Y from the value in AC

                        PC++;
                        break;
                    }
                    case 14:  //Copies the value in the AC to X (CopyToX)
                    {
                        //Sets X equal to value at AC
                        X = AC;

                        PC++;
                        break;
                    }
                    case 15:  //Copies the value from X into the AC (CopyFromX)
                    {
                        //Sets the AC equal to the value in X
                        AC = X;

                        PC++;
                        break;
                    }
                    case 16:  //Copies the value in the AC to Y (CopyToY)
                    {
                        //Sets Y equal to value at AC
                        Y = AC;

                        PC++;
                        break;
                    }
                    case 17:  //Copies the value from Y into the AC (CopyFromY)
                    {
                        //Sets the AC equal to the value in Y
                        AC = Y;

                        PC++;
                        break;
                    }
                    case 18:  //Copies the value in the AC to SP (CopyToSP)
                    {

                        //Sets SP equal to value at AC
                        SP = AC;

                        PC++;
                        break;
                    }
                    case 19:  //Copies the value from SP into the AC (CopyFromSP)
                    {
                        //Sets the AC equal to the value in SP
                        AC = SP;

                        PC++;
                        break;
                    }
                    case 20:  //Jumps to the address provided (Jump addr)
                    {
                        PC += 1;


                        //Takes address value provided and stores it in IR
                        memorySend("r," + PC);
                        memoryRead();

                        //Checks to make sure memory access permissions are correct
                        permissionChecker((IR));

                        //Sets the PC(current address in program runtime) equal to the value we just read into IR
                        PC = IR;
                        break;
                    }
                    case 21:  //Jumps to the address provided only if the value in AC is currently 0 (JumpIfEqual addr)
                    {
                        // If the current value of AC is 0 then jumps to address given
                        if(AC == 0)
                        {
                            //Takes address value provided and stores it in IR
                            PC += 1;


                            //Takes address value provided and stores it in IR
                            memorySend("r," + PC);
                            memoryRead();

                            //Checks to make sure memory access permissions are correct
                            permissionChecker((IR));

                            //Sets the PC(current address in program runtime) equal to the value we just read into IR
                            PC = IR;
                        }
                        else //else it just increments the PC and moves on to next instruction
                        {
                            PC += 2;
                        }
                        break;
                    }
                    case 22:  //Jump to the address provided only if the value in AC is currently not 0 (JumpIfNotEqual addr)
                    {
                        // If the current value of AC is not equal to 0 then jumps to address given
                        if(AC != 0)
                        {
                            //Takes address value provided and stores it in IR
                            PC += 1;
                            memorySend("r," + PC);
                            memoryRead();

                            //Checks to make sure memory access permissions are correct
                            permissionChecker((IR));

                            //Sets the PC(current address in program runtime) equal to the value we just read into IR
                            PC = IR;

                        }
                        else //else it just increments the PC and moves on to next instruction
                        {
                            PC += 2;
                        }
                        break;
                    }
                    case 23:  //Pushes current address onto stack and jumps to new address (Call addr)
                    {
                        PC++;
                        //Store address value provided in IR
                        memorySend("r," + PC);
                        memoryRead();

                        SP--;

                        //Stores the current address onto memory stack then increment current address
                        memorySend("w," + (SP) + "," + PC);

                        //Checks to make sure memory access permissions are correct
                        permissionChecker((IR));

                        //Jumps to new address stored in IR and decrements SP for any future use
                        PC = IR;

                        //SP--;

                        break;
                    }
                    case 24:  //Pop return address from the stack, then jump to it (Ret)
                    {
                        memorySend("r," + SP);
                        memoryRead();

                        SP++;

                        //Checks to make sure memory access permissions are correct
                        permissionChecker((IR));

                        PC = IR;

                        PC++;
                        break;

                    }
                    case 25: //Increments the value in X by 1 (IncX)
                    {
                        X++;
                        PC += 1;
                        break;
                    }
                    case 26:  //Decrements the value in X by 1 (DecX)
                    {
                        X--;
                        PC += 1;
                        break;
                    }
                    case 27:  //Push the current value stored in AC onto the stack (Push)
                    {

                        SP--;

                        memorySend("w," + SP + "," + AC);
                       // System.out.println("SP (push) at: " + SP + " AC: " + AC);

                        PC++;
                        break;
                    }
                    case 28:  //Pop the current value from the stack and put it into AC (Pop)
                    {

                        // Tells the Memory to send the value that is in the stack and put it into IR
                        memorySend("r," + SP);
                        memoryRead();
                        //System.out.println("SP (pop) at: " + SP + " AC: " + IR);

                        SP++;

                        // Puts value found in stack into the AC
                        AC = IR;
                        PC++;

                        break;
                    }
                    case 29:  //Perform system call (Int)
                    {
                        if(current_Mode == false)
                        {

                            //System.out.println("System call has occured");
                            current_Mode = true;
                            memorySend("w,1999," + SP);
                            memorySend("w,1998," + (PC + 1));
                            SP = 1998;
                            PC = 1500;
                        }

                        break;
                    }
                    case 30: // Return from System call (IRet)
                    {
                        if(current_Mode == true)
                        {

                            if((PC < 1500))
                            {
                                currentTimer = 0;
                            }
                            //System.out.println("Interrupt or System call has ended");
                            current_Mode = false;
                            memorySend("r,1999");
                            memoryRead();
                            SP = IR;

                            memorySend("r,1998");
                            memoryRead();
                            PC = IR;
                        }

                        break;
                    }
                    case 50:    //CPU informs Memory that end instruction has be triggered
                    {
                        memorySend("ed,");
                        pwOut.close();
                        scanIn.close();
                        System.exit(0);
                    }
                }

                //Checks if timer interrupt trigger has been met and if it is in usermode
                if (current_Mode == false && currentTimer >= pTimer)
                {

                    current_Mode = true;
                    memorySend("w,1999," + SP);
                    memorySend("w,1998," + PC);
                    SP = 1998;
                    PC = 1000;

                }




            }while(true);



        }
        catch (NumberFormatException ex)
        {
            System.out.println("2nd Argument not a valid input. Please enter a valid integer.");

        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }

    //Reading data from memory into CPU registries
    public static void memoryRead()
    {
        String line = scanIn.nextLine();
        IR = Integer.parseInt(line);
    }

    //Sends data to memory to be either read or written
    public static void memorySend(String data)
    {
        pwOut.write(data + "\n");
        pwOut.flush();
    }

    public static void permissionChecker(int add)
    {
        //Makes sure that system memory is not accessed in user mode
        if(current_Mode == false && (add > 999))
        {
            System.out.println("Memory violation: accessing system memory in user mode");
            memorySend("ed");
        }
    }
}







