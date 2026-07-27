import javax.swing.*;
import java.awt.*;
import java.sql.*;
//update withdrawal limit restricted different to every user
class Withdraw extends JFrame
{
    Withdraw(String username)
    {
        Font f = new Font("Futura", Font.BOLD, 40);
        Font f2 = new Font("Calibri", Font.PLAIN, 22);

        JLabel title = new JLabel("Withdraw Money", JLabel.CENTER);
        JLabel label = new JLabel("Enter Amount:");
        JTextField t1 = new JTextField(10);
        JButton b1 = new JButton("Withdraw");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        label.setFont(f2);
        t1.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        title.setBounds(200, 30, 400, 50);
        label.setBounds(250, 120, 300, 30);
        t1.setBounds(250, 160, 300, 30);
        b1.setBounds(300, 220, 200, 40);
        b2.setBounds(300, 280, 200, 40);

        c.add(title);
        c.add(label);
        c.add(t1);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(
                a->
                {
                    new Home(username);
                    dispose();
                }
        );


        b1.addActionListener(
                a->
                {
                    //PART 1
                    //ROUND 1
                    double balance = 0.0;
                    double wlimit = 0.0;

                    String url = "";

                    try(Connection con = DriverManager.getConnection(url, "", ""))
                    {
                        String sql = "select balance,wlimit from users where username = ?";
                            //wlimit imp
                        try(PreparedStatement pst = con.prepareStatement(sql))
                        {
                            pst.setString(1,username);

                            ResultSet rs = pst.executeQuery();

                            if(rs.next())
                            {
                                balance = rs.getDouble("balance");
                                wlimit = rs.getDouble("wlimit"); //retrieved
                            }

                        }

                    }

                    catch (Exception e)
                    {
                        JOptionPane.showMessageDialog(null,e.getMessage());
                        return;
                    }

                    //PART 2
                    //ROUND 2
                    String s1 = t1.getText();


                    if(s1.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"Please enter amount");
                    }
                    else
                    {
                        double newAmount = Double.parseDouble(s1);

                        if(newAmount>balance)
                        {
                            JOptionPane.showMessageDialog(null,"Not enough Money");
                        }
                        else if(newAmount>wlimit)
                        {
                            JOptionPane.showMessageDialog(null,"limit exceeded");
                        }
                        else
                        {
                            double result;
                            result = balance - newAmount;
                            //PART 3

                            try(Connection con = DriverManager.getConnection(url, "", "")) {
                                String sql = "update users set balance=? where username=?";

                                try (PreparedStatement pst = con.prepareStatement(sql)) {
                                    pst.setDouble(1, result);
                                    pst.setString(2, username);

                                    pst.executeUpdate();
                                    JOptionPane.showMessageDialog(null, "Successfully withdrawn amount.");
                                    t1.setText("");
                                    updatePassbook(username,"Withdraw",-newAmount,-newAmount+balance);
                                }

                            }
                            catch (Exception e)
                            {
                                JOptionPane.showMessageDialog(null,e.getMessage());
                                return;
                            }

                        }

                    }

                }
        );

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Withdraw Money");
    }
    void updatePassbook(String username, String desc, double amount, double balance)
    {
        String url = "";
        try(Connection con = DriverManager.getConnection(url, "", ""))
        {
            String sql = "insert into transactions(username,description,amount,balance) values (?,?,?,?)";

            try(PreparedStatement pst = con.prepareStatement(sql))
            {
                pst.setString(1,username);
                pst.setString(2,desc);
                pst.setDouble(3,amount);
                pst.setDouble(4,balance);
                pst.executeUpdate();
            }

        }
        catch (Exception e) {

            JOptionPane.showMessageDialog(null,e.getMessage());
        }

    }

    public static void main(String[] args) {
        new Withdraw("brina");
    }
}
