import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mancala {

    public static void main(String[] args)throws IOException {
        
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args[1]));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            String task=null;
            task=bufferedReader.readLine();
            line =bufferedReader.readLine();

            int player=Integer.parseInt(line);
            String cutoffdepth=null;
            cutoffdepth=bufferedReader.readLine();
            int cut_of_depth=Integer.parseInt(cutoffdepth);
            String player2=null;
            player2=bufferedReader.readLine();
            String[] play2=player2.split(" ");
            StringBuilder player2Stringval = new StringBuilder();
            player2Stringval.append(play2[play2.length-1]);
            for(int j=play2.length-2;j>=0;j--){
                player2Stringval.append(" ");
                player2Stringval.append(play2[j]);
            
            
           }
            String player1=bufferedReader.readLine();
            String mancala1, mancala2;
            mancala2=bufferedReader.readLine();
           
            String  player22;
            player22 = player2Stringval.toString();
            mancala1=bufferedReader.readLine();
            String combine=mancala2+" "+player1+" "+mancala1+" "+player22;
            
            String[] state=combine.split(" ");
            int[] states=new int[state.length];
            for(int i=0;i<state.length;i++)
                {
                    states[i]=Integer.parseInt(state[i]);
                
                }
               if(task.equals("1")){
            mancala Mancala = new mancala();
            Mancala.greedy(states, player);
            
            }
            
            if(task.equals("2")){
            
            mancala Mancala=new mancala();
            try {
            Mancala.minimax(states,player,cut_of_depth);
            } catch (CloneNotSupportedException ex) {
            Logger.getLogger(mancala.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            }
            
            if(task.equals("3")){
            
            mancala mancala=new mancala();
            try {
            mancala.alphabeta(states, player, cut_of_depth);
            } catch (CloneNotSupportedException ex) {
            Logger.getLogger(mancala.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            }
    }
 
    @SuppressWarnings("unchecked")
    public void greedy(int[] states, int player) throws IOException{
             File file=new File("next_state.txt");
             if(!file.exists()){
                 file.createNewFile();
                          }
             FileWriter fw = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw);
             
             gameval initialState=new gameval(states);
             gameval max = callmove(initialState, player);

             bw.write(max.writeOutput());
             
             bw.close();
        }
    @SuppressWarnings("unchecked")
    public void alphabeta(int[] board,int playernum,int cutoffdepth) throws CloneNotSupportedException, IOException{
        File file=new File("next_state.txt");
             if(!file.exists()){
                 file.createNewFile();
                          }
             FileWriter fw = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw);
        
        File traverseFile=new File("traverse_log.txt");
           if(!traverseFile.exists()){
               traverseFile.createNewFile();
                        }
        FileWriter traverseFW = new FileWriter(traverseFile.getAbsoluteFile());
        BufferedWriter traverseBW = new BufferedWriter(traverseFW);
        
        traverseBW.write("Node,Depth,Value,Alpha,Beta");
        traverseBW.newLine();
        
        
        boolean extraMove=false;
        Map<Integer,Integer>playerMap=new HashMap<Integer,Integer>();
        playerMap.put(1,2);
        playerMap.put(2,1);
        Map<String,String>methodName=new HashMap<String,String>();
        methodName.put("Max","Min");
        methodName.put("Min","Max");
        gameval gamevalObj=new gameval(board);
        
        Stack nodes = new Stack();
        int index;
        int depth;
        int mainPlayer = playernum;
        List<Integer> childList=gamevalObj.getIndexofPlayer(playernum);
        node root = new node(gamevalObj, playernum,0,Integer.MIN_VALUE, "Max", "root",childList,false,Integer.MIN_VALUE,Integer.MAX_VALUE);
        if(root.getG().gameover()){
            root.setChild_list(null);
            root.setBest_state(root.getG());
            root.setValue(root.getG().eval_value(mainPlayer));
            traverseBW.write(root.writeAlphaBetaLog());
        }
        else{
            traverseBW.write("root,0,-Infinity,-Infinity,Infinity");
            traverseBW.newLine();
        }
        node currentObject=null;
        node cloneObject=null;
        nodes.push(root);
        int counter = childList.size();
        
        while(counter!=0){
            currentObject=(node)nodes.pop();
            if(currentObject.getChild_list()!=null && !currentObject.getChild_list().isEmpty()){
                cloneObject=new node(currentObject);
                index=currentObject.getChild_list().get(0);
                currentObject.getChild_list().remove(0);

                nodes.push(currentObject);
                if(currentObject.getG().getBoard()[index]==0)
                    continue;

                extraMove=cloneObject.getG().next_move(index, currentObject.getPlayernum());
                cloneObject.setName(cloneObject.getG().getName(index));
                if(currentObject.isNext_move())
                {
                 depth=currentObject.getDepth();
                }
                else{
                    depth=currentObject.getDepth()+1;
                }
                
                cloneObject.setDepth(depth);
                if(cloneObject.getG().gameover()){
                   cloneObject.setChild_list(null);
                   cloneObject.setValue(cloneObject.getG().eval_value(mainPlayer));
                   traverseBW.write(cloneObject.writeAlphaBetaLog());
                   nodes.push(cloneObject);
                   continue;
                }

                
                if(extraMove)
                {
                   cloneObject.setPlayernum(currentObject.getPlayernum());
                   cloneObject.setChild_list(gamevalObj.getIndexofPlayer(currentObject.getPlayernum()));
                   cloneObject.setMethod(currentObject.getMethod());
                   cloneObject.setNext_move(true);
                   if(cloneObject.getMethod().equals("Max")){
                        cloneObject.setValue(Integer.MIN_VALUE);
                   }
                   if(cloneObject.getMethod().equals("Min")){
                       cloneObject.setValue(Integer.MAX_VALUE);
                   }
                }
                else{
                   cloneObject.setPlayernum(playerMap.get(currentObject.getPlayernum()));
                   cloneObject.setMethod(methodName.get(currentObject.getMethod()));
                   
                   cloneObject.setNext_move(false);
                   if(depth==cutoffdepth){

                   cloneObject.setChild_list(null);
                   cloneObject.setValue(cloneObject.getG().eval_value(mainPlayer));

                   }
                   else{
                       cloneObject.setChild_list(gamevalObj.getIndexofPlayer(cloneObject.getPlayernum()));

                       if(cloneObject.getMethod().equals("Max")){
                           cloneObject.setValue(Integer.MIN_VALUE);
                       }
                       if(cloneObject.getMethod().equals("Min")){
                           cloneObject.setValue(Integer.MAX_VALUE);
                       }

                   }
               }
                   
               traverseBW.write(cloneObject.writeAlphaBetaLog());
               nodes.push(cloneObject);
            }
            else{
                    if(nodes.isEmpty())
                        break;
                    node parentObject=(node) nodes.pop();
                    if(parentObject.getDepth()==0){
                        counter -= 1;                       
                    }
                    if(parentObject.getMethod().equals("Max")){
                        
                        if(parentObject.getValue()<currentObject.getValue()){
                            parentObject.setValue(currentObject.getValue());
                                                        
                            if(parentObject.getDepth()==1){
                                if(currentObject.getBest_state()!=null){
                                    parentObject.setBest_state(currentObject.getBest_state());
                                }
                                else{
                                    parentObject.setBest_state(currentObject.getG());
                                }
                            }
                            if(parentObject.getDepth()==0){
                                
                                if(currentObject.getBest_state()!=null)
                                    parentObject.setBest_state(currentObject.getBest_state());
                            
                                else
                                    parentObject.setBest_state(currentObject.getG());
                            
                            }  
                                
                        }
                        if(parentObject.getValue()>=parentObject.getBeta()){
                            parentObject.setChild_list(null);
                                               
                        }
                        else if(parentObject.getAlpha()<parentObject.getValue()){
                            parentObject.setAlpha(parentObject.getValue());
                                               
                        }
                        }
                    if(parentObject.getMethod().equals("Min"))    
                    {
                        if(parentObject.getValue()>currentObject.getValue()){
                            parentObject.setValue(currentObject.getValue());
                            if(parentObject.getDepth()==1){
                                parentObject.setBest_state(parentObject.getG());

                            }

                        }
                        if(parentObject.getValue()<=parentObject.getAlpha()){
                        
                            parentObject.setChild_list(null);
                                                    
                        }
                        else if(parentObject.getValue()<parentObject.getBeta()){
                                parentObject.setBeta(parentObject.getValue());
                        
                        }

                    }
                    traverseBW.write(parentObject.writeAlphaBetaLog());
                    nodes.push(parentObject);
                }
            }
       
            bw.write(root.getBest_state().writeOutput());
            bw.close();
            traverseBW.close();
    
    
    }
    @SuppressWarnings("unchecked")
    public void minimax(int[] board,int playernum,int cutoffdepth) throws CloneNotSupportedException, IOException{
          File file=new File("next_state.txt");
             if(!file.exists()){
                 file.createNewFile();
                          }
             FileWriter fw = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw);
        
        File traverseFile=new File("traverse_log.txt");
           if(!traverseFile.exists()){
               traverseFile.createNewFile();
                        }
        FileWriter traverseFW = new FileWriter(traverseFile.getAbsoluteFile());
        BufferedWriter traverseBW = new BufferedWriter(traverseFW);
        
        traverseBW.write("Node,Depth,Value");
        traverseBW.newLine();

        
        boolean extraMove=false;
        Map<Integer,Integer>playerMap=new HashMap<Integer,Integer>();
        playerMap.put(1,2);
        playerMap.put(2,1);
        Map<String,String>methodName=new HashMap<String,String>();
        methodName.put("Max","Min");
        methodName.put("Min","Max");
        gameval gamevalObj=new gameval(board);
        
        Stack nodes = new Stack();
        
        int index;
        int depth;
        int mainPlayer = playernum;
        List<Integer> childList=gamevalObj.getIndexofPlayer(playernum);
        node root = new node(gamevalObj, playernum,0,Integer.MIN_VALUE, "Max", "root",childList,false);
            if(root.getG().gameover()){
                    root.setChild_list(null);
                    root.setBest_state(root.getG());
                    root.setValue(root.getG().eval_value(mainPlayer));
                    traverseBW.write(root.writeLog());
            }
            else{
                traverseBW.write("root,0,-Infinity");
                traverseBW.newLine();
            }
        node currentObject=null;
        node cloneObject=null;
        nodes.push(root);
        int counter = childList.size();
        
        while(counter!=0){
            currentObject=(node)nodes.pop();
            if(currentObject.getChild_list()!=null && !currentObject.getChild_list().isEmpty()){
                cloneObject=new node(currentObject);
                index=currentObject.getChild_list().get(0);
                currentObject.getChild_list().remove(0);
                nodes.push(currentObject);
                if(currentObject.getG().getBoard()[index]==0)
                    continue;

                extraMove=cloneObject.getG().next_move(index, currentObject.getPlayernum());
                cloneObject.setName(cloneObject.getG().getName(index));
                 if(currentObject.isNext_move())
                {
                 depth=currentObject.getDepth();
                }
                else{
                    depth=currentObject.getDepth()+1;
                }
                 cloneObject.setDepth(depth);
                if(cloneObject.getG().gameover()){
                   cloneObject.setChild_list(null);
                   cloneObject.setValue(cloneObject.getG().eval_value(mainPlayer));
                   traverseBW.write(cloneObject.writeLog());
                   nodes.push(cloneObject);
                   continue;
                }

               
                if(extraMove)
                {
                   cloneObject.setPlayernum(currentObject.getPlayernum());
                   cloneObject.setChild_list(gamevalObj.getIndexofPlayer(currentObject.getPlayernum()));
                   cloneObject.setMethod(currentObject.getMethod());
                   cloneObject.setNext_move(true);
                   if(cloneObject.getMethod().equals("Max")){
                        cloneObject.setValue(Integer.MIN_VALUE);
                   }
                   if(cloneObject.getMethod().equals("Min")){
                       cloneObject.setValue(Integer.MAX_VALUE);
                   }
                }
                else{
                   cloneObject.setPlayernum(playerMap.get(currentObject.getPlayernum()));
                   cloneObject.setMethod(methodName.get(currentObject.getMethod()));
                   
                   cloneObject.setNext_move(false);
                   if(depth==cutoffdepth){

                   cloneObject.setChild_list(null);
                   cloneObject.setValue(cloneObject.getG().eval_value(mainPlayer));

                   }
                   else{
                       cloneObject.setChild_list(gamevalObj.getIndexofPlayer(cloneObject.getPlayernum()));

                       if(cloneObject.getMethod().equals("Max")){
                           cloneObject.setValue(Integer.MIN_VALUE);
                       }
                       if(cloneObject.getMethod().equals("Min")){
                           cloneObject.setValue(Integer.MAX_VALUE);
                       }

                   }
               }
                   
               traverseBW.write(cloneObject.writeLog());
               nodes.push(cloneObject);
            }
            else{
                    if(nodes.isEmpty())
                        break;
                    node parentObject=(node) nodes.pop();
                    if(parentObject.getDepth()==0){
                        counter -= 1;                       
                    }
                    if(parentObject.getMethod().equals("Max")){
                        
                        if(parentObject.getValue()<currentObject.getValue()){
                            parentObject.setValue(currentObject.getValue());
                            
                            if(parentObject.getDepth()==1){
                                if(currentObject.getBest_state()!=null){
                                    parentObject.setBest_state(currentObject.getBest_state());
                                }
                                else{
                                    parentObject.setBest_state(currentObject.getG());
                                }
                            }
                            if(parentObject.getDepth()==0){
                                
                                if(currentObject.getBest_state()!=null)
                                    parentObject.setBest_state(currentObject.getBest_state());
                            
                                else
                                    parentObject.setBest_state(currentObject.getG());
                            
                            }  
                                
                        }
                    }
                    if(parentObject.getMethod().equals("Min"))    
                    {
                        if(parentObject.getValue()>currentObject.getValue()){
                            parentObject.setValue(currentObject.getValue());
                            if(parentObject.getDepth()==1){
                                parentObject.setBest_state(parentObject.getG());

                            }

                        }

                    }
                    traverseBW.write(parentObject.writeLog());
                    nodes.push(parentObject);
                }
            }
       
            bw.write(root.getBest_state().writeOutput());
            bw.close();
            traverseBW.close();
        }

    public gameval callmove(gameval g1,int playernum)
    {         
        if(g1.gameover()==true)
        {
            return g1;
        
        }
    
        gameval maxBoard=g1;
        int start,end;
        boolean turn=false;
        boolean firstLegalMove=true;
        if(playernum==1)
        {
            start=1;
            end=g1.getBoardLength()/2;
            for(int i=start;i<end;i++)
            {   
                gameval game=new gameval(g1);
                if(game.getBoard()[i]==0)
                {
                    continue;
                     
                } 
                turn=game.next_move(i, playernum);
                if(game.gameover()==true){
                    return game;
                }
                for(int j:game.getBoard()){
                    
                }
                
                if(turn==true)
                    {
                        game = callmove(game,playernum);
                    }
                if(firstLegalMove==true){
                                maxBoard=game;
                                firstLegalMove=false;
                }
                if(game.eval_value(playernum)> maxBoard.eval_value(playernum))
                    {
                            maxBoard=game;
                    }
                
            }
        }
        if(playernum==2)
        {
            end= g1.getBoardLength()/2;
            start= g1.getBoardLength()-1;
            for(int i=start;i>end;i--)
            {
                gameval game=new gameval(g1);
                 if(game.getBoard()[i]==0)
                {
                    continue;
                     
                } 
                turn=game.next_move(i, playernum);
                if(game.gameover()==true){
                return game;
                } 
                if(turn==true)
                {
                game = callmove(game,playernum);
                }
                if(firstLegalMove==true){
                                maxBoard=game;
                                firstLegalMove=false;
                }
                if(game.eval_value(playernum)>maxBoard.eval_value(playernum))
                {
                    maxBoard=game;

                }
            }
   
        }
        return maxBoard;
    }
   
public class gameval{
            int[] board;
            Map<Integer, String> index_to_name=null;
            
        public void createIndexMap(){
            index_to_name = new HashMap<Integer, String>();
            for(int i=1;i<board.length/2;i++)
              {
                  int j=i+1;
                  String name=Integer.toString(j);
                  index_to_name.put(i,("B"+name));

              }
              for(int i=board.length-1;i>board.length/2;i--)
              {
                 int j=(board.length-i)+1;
                  String name=Integer.toString(j);
                  index_to_name.put(i,("A"+name)); 
              } 
        }
            
        public gameval(int[] board )
        {
            this.board=board;
            createIndexMap();
              
        }
        public String getName(int in){
           return index_to_name.get(in);
        }
        public int[] getBoard(){
            return this.board;
        }
        
        public gameval(gameval g)
        {
                this.board=g.getBoard().clone();
                createIndexMap();
        }
        public int getBoardLength(){
        
        return board.length;
        
        }
        @SuppressWarnings("unchecked")
        public List<Integer> getIndexofPlayer(int playerNum){
        
            int startIndex,endIndex;
            List<Integer> Index=new ArrayList();
            if(playerNum==1){
                    startIndex=1;
                    endIndex=board.length/2-1;

                    for(int i=startIndex;i<=endIndex;i++){
                            Index.add(i);

                    }

                }
        if(playerNum==2){
            startIndex=board.length-1;
            endIndex=board.length/2+1;
            for(int i=startIndex;i>=endIndex;i--){
            Index.add(i);
            }
        }    
        return Index;
     }
    
   public boolean next_move(int start_index, int playernum)
   {
       int skip=-1,mancalapit=-1;
       if(playernum==1){
           skip=0;
           mancalapit=board.length/2;
       }
       if(playernum==2){
           skip=board.length/2; 
           mancalapit=0;
       }
       int count=board[start_index];
       board[start_index] =0;
       while(count!=0)
       {
            for(int i=start_index+1;i<board.length;i++)
            {
                
                if(i==skip)
                    continue;
                board[i]++;
                
                count--;                
                if(count==0)
                {      
                    if((board[i]==1)&&(i!=0)&&(i!=board.length/2)){
                        if((playernum==1)&&(i<board.length/2)&&(i>0))
                        {
                            board[board.length/2]=board[board.length/2]+board[i]+board[board.length-i];
                            board[board.length-i]=0;
                            board[i]=0;  
                        }
                        if((playernum==2)&&(i>board.length/2)&&(i<board.length)){
                            board[0]=board[0]+board[i]+board[board.length-i];
                            board[board.length-i]=0;
                            board[i]=0;
                                             
                        }
                
                    }                        
                    if(i==mancalapit){
                        return true;
                       }   
                    break;          
                }        
            }
            start_index=-1;
        }
        return false;  
    }
    public boolean gameover()
    {   
        int sum=0;
                for (int j=1;j<board.length/2;j++)
                {
                sum=sum+board[j];
                }                
                if(sum==0){
               
               for(int i=board.length-1;i>board.length/2;i--){
               board[0]=board[0]+board[i];
               board[i]=0;
                             
                    }
                return true;
                        }
        sum=0;
            for(int j=board.length-1;j>board.length/2;j--){
                sum=sum+board[j];
                        }
        if(sum==0){
            for(int i=1;i<board.length/2;i++)
            {
            board[board.length/2]=board[board.length/2]+board[i];
            board[i]=0;
                      }
           return true;
        }
        return false;
        }
     
   public int eval_value(int playernum)
   {    
       int value=0;
         if(playernum==1)
         {
               value=board[(board.length/2)]-board[0];  
         }
         
         if(playernum==2)
         {
             value=board[0]-board[(board.length/2)];
         }
         return value;
   }
   
   public String writeOutput(){
             StringBuilder str=new StringBuilder();
             str.append(Integer.toString(getBoard()[getBoardLength()-1]));
             for(int i=getBoardLength()-2;i>getBoardLength()/2;i--){
                    str.append(" ");
                    str.append(Integer.toString(getBoard()[i]));

               }                       
             str.append(System.getProperty("line.separator"));
             str.append(Integer.toString(getBoard()[1]));
             for(int i=2;i<getBoardLength()/2;i++)
             {
                str.append(" ");
                 str.append(Integer.toString(getBoard()[i]));
                
             }
             str.append(System.getProperty("line.separator"));
             str.append(Integer.toString(getBoard()[0]));
             str.append(System.getProperty("line.separator"));
             str.append(Integer.toString(getBoard()[getBoardLength()/2]));
             str.append(System.getProperty("line.separator"));
             
   
   
   return(str.toString());
   }

}

public class node {
  
    gameval g;
            gameval best_state=null;
            int  depth,playernum,value;
            String method,name;
            boolean next_move;
            List<Integer> child_list;
            int alpha,beta;

        public node(gameval gamevalObj, int playernum, int depth, int value, String method, String name, List<Integer> childList,boolean next_move) {
            this.g=gamevalObj;
            this.depth=depth;
            this.value=value;
            this.playernum=playernum;
            this.child_list=childList;
            this.method=method;
            this.name=name;   
            this.next_move=next_move;
           }
         public node(gameval gamevalObj, int playernum, int depth, int value, String method, String name, List<Integer> childList,boolean next_move,int alpha, int beta) {
            this.g=gamevalObj;
            this.depth=depth;
            this.value=value;
            this.playernum=playernum;
            this.child_list=childList;
            this.method=method;
            this.name=name;   
            this.next_move=next_move;
            this.alpha=alpha;
            this.beta=beta;
           }
        

        public gameval getBest_state() {
            return best_state;
        }

        public void setBest_state(gameval best_state) {
            this.best_state = best_state;
        }
        @SuppressWarnings("unchecked")
        public node(node n){
        
                    this.g= new gameval(n.getG());
                    this.depth=n.getDepth();
                    this.playernum=n.getPlayernum();
                    this.value=n.value;
                    ArrayList<Integer> children = (ArrayList) n.getChild_list();
                    this.child_list= (ArrayList) children.clone();
                    this.method=n.getMethod();
                    this.name=n.getName(); 
                     this.next_move=n.next_move;
                    this.alpha=n.alpha;
                    this.beta=n.beta;
                     
                     
        }

        public void setG(gameval g) {
            this.g = g;
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        public int getBeta() {
            return beta;
        }

        public void setBeta(int beta) {
            this.beta = beta;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setPlayernum(int playernum) {
            this.playernum = playernum;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setName(String name) {
            this.name = name;
            
        }

        public void setNext_move(boolean next_move) {
            this.next_move = next_move;
        }

        public void setChild_list(List<Integer> child_list) {
            this.child_list = child_list;
        }

        public List<Integer> getChild_list() {
            return child_list;
        }

        public gameval getG() {
            return g;
        }

        public int getDepth() {
            return depth;
        }

        public int getPlayernum() {
            return playernum;
        }

        public String getMethod() {
            return method;
        }

        public String getName() {
            return name;
        }

        public boolean isNext_move() {
            return next_move;
        }

       public String writeLog(){
                StringBuilder result=new StringBuilder();
                String nam,dep,val;
                if(value==Integer.MAX_VALUE){

                    val="Infinity";

                }
                else if(value==Integer.MIN_VALUE){

                    val="-Infinity";
                }
                else
                     val=Integer.toString(value);

                dep=Integer.toString(depth);
                result.append(name);
                result.append(",");
                result.append(dep);
                result.append(",");
                result.append(val);

                result.append(System.getProperty("line.separator"));

                return result.toString();
          }
       public String writeAlphaBetaLog(){
       
                StringBuilder result=new StringBuilder();
                String nam,dep = null,val,alp,bet;
                if(value==Integer.MAX_VALUE){

                    val="Infinity";

                }
                else if(value==Integer.MIN_VALUE){

                    val="-Infinity";
                }
                else
                     val=Integer.toString(value);

                if(alpha==Integer.MIN_VALUE){
                    alp="-Infinity";            
                }
                else{
                    alp=Integer.toString(alpha);
                
                }
                if(beta==Integer.MAX_VALUE){
                    bet="Infinity";
                
                }
                else{
                    bet=Integer.toString(beta);
                }
                dep=Integer.toString(depth);
                result.append(name);
                result.append(",");
                result.append(dep);
                result.append(",");
                result.append(val);
                result.append(",");
                result.append(alp);
                result.append(",");
                result.append(bet);

                result.append(System.getProperty("line.separator"));

                return result.toString();
       
       
       
       
       
       
       }


}


}
