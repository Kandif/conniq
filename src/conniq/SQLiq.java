
package conniq;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is dedicated for mysql management.
 * @author Kandif
 */
public class SQLiq {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String DRIVER = "com.mysql.jdbc.Driver";
    private String TABLE = "";
    private int columnenumber=0;
    private int rownumber=0;
    
    public void connectSql(String host,String user, String password){
        try { 
            Class.forName(DRIVER);
            connect = DriverManager.getConnection("jdbc:mysql://"+host+"/",user,password);
            statement = connect.createStatement();
        } catch (SQLException ex) {
            System.out.println("Error while contectinh to host: "+ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Error while changing Driver: "+ex);
        }  
    }
    
    public void connectSql(String host,String datebase,String user, String password){
        try { 
            Class.forName(DRIVER);
            connect = DriverManager.getConnection("jdbc:mysql://"+host+"/"+datebase,user,password);
            statement = connect.createStatement();
        } catch (SQLException ex) {
            System.out.println("Error while contectinh to host: "+ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Error while changing Driver: "+ex);
        }  
    }
    
    public void selectDataBase(String name){
        try {
            String query = "USE "+name;
            statement.execute(query);
        } catch (SQLException ex) {
            
        }
    }
    
    public void setDefaultTable(String table){
        TABLE = table;
        try{
            ResultSet rs = statement.executeQuery("select count(*) from "+TABLE);
            rs.next();
            rownumber = rs.getInt(1);
            ResultSetMetaData rsmd;
            rs = statement.executeQuery("select * from "+TABLE+" LIMIT 1");
            rsmd = rs.getMetaData();
            columnenumber = rsmd.getColumnCount();
            //System.out.println();
        } catch (SQLException ex) {
            System.out.println("Error when checking table: "+ex);
        }
        

        
    }
    
    public String[][] getValues(){
        try {        
            preparedStatement = connect.prepareStatement("SELECT * FROM "+TABLE);
            resultSet = preparedStatement.executeQuery();            
            String tab[][] = new String[getRownumber()][getColumnenumber()];
            while (resultSet.next()) { 
                int row = resultSet.getRow();
                for(int c=1;c<=getColumnenumber();c++){
                    tab[row-1][c-1] = resultSet.getString(c);
                }
            }
            return tab;
        } catch (SQLException ex) {
            System.out.println("Error while geting values: "+ex);
            return null;
        }
            
    }
    
    public void insertRecord(boolean auto,String ...sql){
        //System.out.println(sql.length+" "+getColumnenumber());
        if(sql.length==getColumnenumber() || (sql.length+1==getColumnenumber()&&auto)){
            try {
                String query = "INSERT INTO "+TABLE+" VALUES (";
                for(int i=0;i<sql.length;i++){   
                   if(i==0){
                       if(auto) query =query.concat(""+sql[i]+"");
                       else query =query.concat("'"+sql[i]+"'");
                   } 
                   else query =query.concat(", '"+sql[i]+"'"); 
                }
                query =query.concat(")");
                //System.out.println(query);
                statement.executeUpdate(query);
            } catch (SQLException ex) {
                System.out.println("Error while insert record: "+ex);
            }
            rownumber++;
        }
    }
    
    public void gaga(int tab[]){
        
    }
    
    public void updateRecord(String condition,String ...sql){
        String query = "UPDATE "+TABLE+" SET ";
        for(int i=0;i<sql.length;i++){   
            if(i==0)query = query.concat(sql[i]+" "); 
            else query = query.concat(", "+sql[i]+" "); 
        }
        query = query.concat(" WHERE "+condition); 
        //System.out.println(query);
        try {
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println("Error while updating records: "+ex);
        }
    }
    
    public void deleteRecord(String condition){
        try {
            String query = "DELETE FROM "+TABLE+" WHERE "+condition;
            statement.executeUpdate(query);
            rownumber--;
        } catch (SQLException ex) {
            System.out.println("Error while deleting record: "+ex);
        }
    }
    
//    public String getValues(int columnes){
//        try {        
//            preparedStatement = connect.prepareStatement("SELECT * FROM "+TABLE);
//            resultSet = preparedStatement.executeQuery();
//            resultSet.next();
//            return resultSet.getString("tadam");
//        } catch (SQLException ex) {
//            System.out.println("Error while geting values: "+ex);
//            return null;
//        }
//            
//    }
    
    public void createTable(boolean idactived,String ...sql){
         String sqls = "CREATE TABLE "+sql[0];
         if(idactived) sqls = sqls.concat("(id INTEGER not NULL " );
                   
         for(int i=1;i<sql.length;i++){
             if(sql[i].startsWith("INT"))
                 sqls = sqls.concat(", "+sql[i].substring(3)+" INTEGER ");
             if(sql[i].startsWith("STR"))
                 sqls = sqls.concat(", "+sql[i].substring(3)+" VARCHAR(255) ");       
         }        
         if(idactived) sqls = sqls.concat(", PRIMARY KEY ( id ))");
         else sqls = sqls.concat(")");
        try {         
            statement.executeUpdate(sqls);
        } catch (SQLException ex) {
            System.out.println("Error while creating table: "+ex);
        }
    }
    
    public void disconnectSql(){
        try { 
            statement.close();
            connect.close(); 
        } catch (SQLException ex) {
            System.out.println("Error while disconnecting: "+ex);
        }
        
    }

    /**
     * @return the columnenumber
     */
    public int getColumnenumber() {
        return columnenumber;
    }

    /**
     * @return the rownumber
     */
    public int getRownumber() {
        return rownumber;
    }
    
}
