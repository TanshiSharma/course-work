package backtracking;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Backtracking {
        public static int count_std=1;
       public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader bufferedReader=new BufferedReader(new  FileReader(args[1]));
        StringBuffer stringBuffer=new StringBuffer();
        String line=null;
        String task=null;
        line=bufferedReader.readLine();
              
        Map<String,List<String>> knowledgeBase=new HashMap<String,List<String>>();
        List<String> Querylist=new ArrayList<String>();
        for(int i=0;i<Integer.parseInt(line);i++)
        {
         task=bufferedReader.readLine();
         Querylist.add(task.trim());
        }
        line=bufferedReader.readLine();

        for(int i=0;i<Integer.parseInt(line);i++)
        {
            String predicate=null;
            task=bufferedReader.readLine().trim(); 
           
            if(task.contains("=>"))
            {
                String getFirst=task.split("=>")[1].toString();
                predicate=getPredicate(getFirst);
           
            }
            else{
                predicate=getPredicate(task);      
                }
            
            if(knowledgeBase.containsKey(predicate))
            {
                List<String> temp=knowledgeBase.get(predicate);
                temp.add(task);
                knowledgeBase.put(predicate,temp);
            }
            else{
            
                List<String> temp=new ArrayList<String>();
                temp.add(task);
                knowledgeBase.put(predicate,temp);
                             
            }
            
            
        }

        for(String key:knowledgeBase.keySet()){
        
        List<String> facts=new ArrayList<String>();
        List<String> rules=new ArrayList<String>();
        List<String> temp=new ArrayList<String>();
        
        for(String clause: knowledgeBase.get(key)){
            if(clause.contains("=>")){         
                rules.add(clause.trim());
            }
            else{
                facts.add(clause.trim());
            }
        }
        temp.addAll(facts);
        temp.addAll(rules);
        knowledgeBase.put(key,temp);
        }
    }
   
    public List<Map<String,String>> backwardChaining(Map<String,List<String>> kb,List<String> goalList,Map<String,String> theta,List<String> facts,List<String>infiniteLoop){

        List<Map<String, String>> answerList = new ArrayList<Map<String, String>>();
        List<String> newGoals=new ArrayList<String>();

        if(goalList.isEmpty()){

            answerList.add(theta);
            return answerList;
        }
        String currentGoal=substitution(goalList.get(0),theta);
        goalList.remove(0);
        if(infiniteLoop.contains(currentGoal)){
            return answerList;
        }
        if(!facts.contains(currentGoal)){
          infiniteLoop.add(currentGoal);

        }
        String predCG=getPredicate(currentGoal);
        List<String> currentRules=kb.get(currentGoal);
        if(currentRules.isEmpty()){
        return answerList;
        }

        for(String rule:currentRules){
            Map<String,List<String>> stdrule=standardisation(rule);
            List<String> lhs=stdrule.get("lhs");
            String rhs=stdrule.get("rhs").get(0);
           Map<String,String> copytheta=thetacopy(theta);
            unification(currentGoal,rhs,copytheta);
            if(!copytheta.isEmpty()){
                if((lhs.isEmpty())&&(goalList.size()>0)){
                    newGoals.addAll(goalList);
                    List<Map<String,String>> lowlevel=backwardChaining(kb,goalcopy(newGoals),thetacopy(copytheta),facts,infiniteLoop);
                    if(lowlevel.size()==0){

                    newGoals.clear();

                    }
                    answerList.addAll(lowlevel);

                }
                else if((!lhs.isEmpty())&&(goalList.size()==0)){
                    answerList.add(copytheta);

                }
                else{
                     newGoals.addAll(lhs);
                     newGoals.addAll(goalList);
                    List<Map<String,String>> lowlevel=backwardChaining(kb,goalcopy(newGoals),thetacopy(copytheta),facts,infiniteLoop);
                    if(lowlevel.size()==0){

                    newGoals.clear();

                    }
                    answerList.addAll(lowlevel);

                }
            }
    
       }
        return answerList;
    }

    public static Map<String,String> thetacopy(Map<String,String> theta){

        Map<String,String> copyTheta=new HashMap<String,String>();
        for(String key:theta.keySet()){
            copyTheta.put(key,theta.get(key));
        }
        return copyTheta;

    }
    public static List<String> goalcopy(List<String> goal){

        List<String> copyGoals=new ArrayList<String>();
        for(String s:goal){
            copyGoals.add(s);
        
        }
        return copyGoals;
    }
      
    public static String getPredicate(String str){

        String[] split=str.split("\\(");
        String result=split[0];
        System.out.println(result);
        return result;
    }
    public static boolean isVariable(String name)
    {
        return Character.isLowerCase(name.charAt(0));
    }
    public static void unificationvariable(String str,String str1,Map<String,String> theta){

        if(str.equals(str1)){
            return;
        }
        if(isVariable(str)){
            if(theta.containsKey(str))
            {
                unificationvariable(theta.get(str),str1,theta);
            }
            else if(theta.containsKey(str1)){
                unificationvariable(str,theta.get(str1),theta);
            }
            else{
                theta.put(str,str1);
            }

        }
        else if(isVariable(str1))
        {
            if(theta.containsKey(str1))
            {
                unificationvariable(theta.get(str1),str,theta);
            }
            else if(theta.containsKey(str)){
                unificationvariable(str1,theta.get(str),theta);
            }
            else{
                theta.put(str1,str);
            }

        }
        else if(!str.equals(str1)){

            theta.clear();
        }


    }
    public static Map<String,String> unification(String str, String str1,Map<String,String> update){
        String[] str2=str.split("\\(");
        String[] str3=str2[1].toString().split("\\)");
        String[] str4=str3[0].toString().split(",");
        String[] str5=str1.split("\\(");
        String[] str6=str5[1].toString().split("\\)");
        String[] str7=str6[0].toString().split(",");


        for(int i=0;i<str4.length;i++){
        unificationvariable(str4[i],str7[i],update);
        if(update.isEmpty()){
            break;
        }
        }
        for(String key:update.keySet()){
            String val=update.get(key);
            if(update.containsKey(val)&&isVariable(val)){
                update.put(key,update.get(val));

            }

        }
        return update;

    }
 
    public static String substitution(String str, Map<String,String> Theta){
        List<String>resultList=new ArrayList();
        String[] split1=str.split("\\(");
        String[] split2=split1[1].toString().split("\\)");
        String[] split3=split2[0].toString().split(",");
        for(int i=0;i<split3.length;i++)
        {
            System.out.println(Theta.get(split3[i]));
            if((Theta.get(split3[i])!=null)&&(isVariable(split3[i])))
            {
                resultList.add(Theta.get(split3[i]));
            }
            else{

                resultList.add(split3[i]);

            }
        }
        StringBuilder result=new StringBuilder();
        result.append(split1[0]);
        result.append("(");
        result.append(resultList.get(0));
        for(int i=1;i<resultList.size();i++){
            result.append(",");
            result.append(resultList.get(i));

        }
        result.append(")");
        return result.toString();
    }
 
    public static List<String> getParams(String str){

     String[] split1=str.split("\\(");
     String[]split2=split1[1].toString().split("\\)");
     String[] split3=split2[0].toString().split(",");
     List<String>params=new ArrayList<String>();

    for(int i=0;i<split3.length;i++){

        params.add(split3[i]);


    }
    return params;
    }

    public static Map<String,List<String>> standardisation(String str){
        List<String> rhs=new ArrayList<String>();
        List<String> lhs=new ArrayList<String>();
        List<String> params=new ArrayList<String>();

        if(str.contains("=>")){

            String[] split1=str.split("=>");
            rhs.add(split1[1].trim());
            List<String> rhsparams=getParams(split1[1].trim());
            for(String s:rhsparams){
                if(!params.contains(s))
                {
                    params.add(s);
                }

            }

            String[] split2=split1[0].toString().split("\\^");
            for(int i=0;i<split2.length;i++){
                lhs.add(split2[i].trim());
                List<String> lhsparams=getParams(split2[i].trim());
                for(String s:lhsparams){
                    if(!params.contains(s)){

                        params.add(s);

                    }

                    }

                }
        }
        else{

            rhs.add(str);
            List<String> rhsparams=getParams(str);
            for(String s:rhsparams){
                if(!params.contains(s))
                {
                    params.add(s);


                }

            }

    }
    Map<String,String> temp=new HashMap<String,String>();
    for(int i=0;i<params.size();i++){
        StringBuilder sb=new StringBuilder();
        sb.append("v");
        sb.append(count_std);
        temp.put(params.get(i), sb.toString());
        count_std++;

    }
    List<String> finallhs=new ArrayList<String>();
    List<String> finalrhs=new ArrayList<String>();

        for(int i=0;i<lhs.size();i++){
            String predicate=getPredicate(lhs.get(i));
            List<String> parameters= getParams(lhs.get(i));
            StringBuilder sbb=new StringBuilder();
            sbb.append(predicate);
            sbb.append("(");
             if(isVariable(parameters.get(0)))
                sbb.append(temp.get(parameters.get(0)));
                else
                    sbb.append(parameters.get(0));

            for(int j=1;j<parameters.size();j++){
                if(isVariable(parameters.get(j))){
                    sbb.append(",");
                sbb.append(temp.get(parameters.get(j)));

                }
                else
                    sbb.append(parameters.get(j));
            }
            sbb.append(")");

            finallhs.add(sbb.toString());
        }

        String predicate_rhs=getPredicate(rhs.get(0));
        List<String> param_rhs=getParams(rhs.get(0));
        StringBuilder sbrhs=new StringBuilder();
        sbrhs.append(predicate_rhs);
        sbrhs.append("(");
        if(isVariable(param_rhs.get(0)))
            sbrhs.append(temp.get(param_rhs.get(0)));
        else
            sbrhs.append(param_rhs.get(0));

        for(int j=1;j<param_rhs.size();j++){
            if(isVariable(param_rhs.get(j))){
                sbrhs.append(",");
                sbrhs.append(temp.get(param_rhs.get(j)));

            }else
                    sbrhs.append(param_rhs.get(j));

        }
        sbrhs.append(")");
        finalrhs.add(sbrhs.toString());

        Map<String,List<String>> std=new HashMap<String, List<String>>();
        std.put("rhs",finalrhs);
        std.put("lhs",finallhs);
        return std;
    }

 
}