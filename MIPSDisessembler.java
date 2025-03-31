public class MIPSDisessembler {


    //variables created for all parts of the I and R format
    private int opcode;
    private int s1;
    private int s2;
    private int ds;
    private int x;
    private int func;
    private int sd;
    private String function;
    private short offset;
    private int address;
    private int targetAddress;

    //get method for the func bitwise value
    public int getFunc(int inst) {
        return func;
    }

    //get method for the offset value
    public short getOffset(int inst) {
        return offset;
    }

    //get method for the opcode value
    public int getOpcode(int inst) {
        opcode = (inst & 0xFC000000) >>> 26; //bitwise AND operation to mask and find the opcode value
        return opcode;
    }

    //method to set the address
    public int setAddress(int hexNum) {
        return this.address = hexNum;
    }

    //method to determine if instruction is a r or i format
    public int determineFormat(int opcode) {
        if (opcode == 0) {
            return 0; //it is an r-format
        }
        else {
            return 1; //it is an i-format
        }
    }

    //format to determine the function of the instruction for R-format
    public String determineFunc(int func) {
        if (func == 32) {
            function = "add";
        }
        else if (func == 0x24) {
            function = "and";
        }
        else if (func == 0x25) {
            function = "or";
        }
        else if (func == 0x22) {
            function = "sub";
        }
        else if (func == 0x2a) {
            function = "slt";
        }
        else {
            return null;
        }
        return function;
    }


    //method to determine the functin for the I-format instruction
    public String determineOpcode(int opcode) {
        if (opcode == 0x23) {
            function = "lw";
        }
        else if (opcode == 0x2b) {
            function = "sw";
        }
        else if (opcode == 0x04) {
            function = "beq";
        }
        else if (opcode == 0x05) {
            function = "bne";
        }
        else {
            return null;
        }
        return function;
    }

    //method using bitwise AND operation to mask and find the different registers/elements of the r-format instruction
    public void extractRFormat(int inst) {
        s1 = (inst & 0x03E00000) >>> 21;
        s2 = (inst & 0x001F0000) >>> 16;
        ds = (inst & 0x0000F800) >>> 11;
        x = (inst & 0x000007C0) >>> 6;
        func = (inst & 0x0000003F);
    }

    //method using bitwise AND operation to mask and find the different registers/elements of the i-format instruction
    public void extractIFormat(int inst) {
        s1 = (inst & 0x03E00000) >>> 21;
        sd = (inst & 0x001F0000) >>> 16;
        int extractedOffset = (inst & 0x0000FFFF); //masked offset set to int for comparing to int instruction
        offset = (short) extractedOffset; //offset set to singed short
    }

    //method to increment address by hex 4
    public int incrementAddress() {
        return this.address = this.address + 0x4;
    }

   
    //method to calcuate target address for branches
    public int calculateTargetAddress() {
        targetAddress = this.address + 0x4;
        offset = (short) (offset << 2);
        targetAddress = targetAddress + offset;
        return targetAddress;

    }
        

    //method to format output string for all r-format instruction
    public String toStringRFormat() {
        return String.format("%X %s $%d, $%d, $%d", this.address, function, ds, s1, s2);
    }

    //method to format output string for i-format instruction
    public String toStringIFormat() {
        return String.format("%X %s $%d, %d($%d)", this.address, function, sd, offset, s1);
    }

    //method to format output string for branch instruction
    public String toStringBranch() {
        return String.format("%X %s $%d, $%d, %s %X", this.address, function, sd, s1, "address", targetAddress);
    }
    

    public static void main(String[] args) throws Exception {

        //array of instructions to be disessembled
        int[] machineInstruction = {0x032BA020, 0x8CE90014, 0x12A90003, 0x022DA822, 0xADB30020, 0x02697824, 0xAE8FFFF4,
            0x018C6020, 0x02A4A825, 0x158FFFF7, 0x8ECDFFF0};


        MIPSDisessembler disessembler = new MIPSDisessembler(); //create object disessembler
        disessembler.setAddress(0x9A040); //set intial address to hex 9A040


        //loop through the instruction array
        for (int i = 0; i < 11; i++) {
            int opcode = disessembler.getOpcode(machineInstruction[i]);
            int format = disessembler.determineFormat(opcode);
            
            if (format == 0) { //for r-format
                disessembler.extractRFormat(machineInstruction[i]);
                int func = disessembler.getFunc(machineInstruction[i]);
                disessembler.determineFunc(func);
                System.out.println(disessembler.toStringRFormat());
                
            }
            else if (format == 1) {//for i-format
                disessembler.extractIFormat(machineInstruction[i]);
                String iFunc = disessembler.determineOpcode(opcode);
                if (opcode == 0x4 || opcode == 0x5) { //for branches
                    disessembler.calculateTargetAddress();
                    System.out.println(disessembler.toStringBranch());
                    
                }
                else {
                    System.out.println(disessembler.toStringIFormat());
                }
                
            }
            disessembler.incrementAddress();
        }
        
    }
}
