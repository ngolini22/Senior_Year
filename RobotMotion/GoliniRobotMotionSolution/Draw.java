/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Above is from where I got boilerplate code for drawing methods
 *
 */


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Draw extends JFrame{
    ArrayList<Line2D> links;
    ArrayList<Rectangle> obstacles;
    ArrayList<Robot.ArmNode> alpha;
    HashMap<Robot.ArmNode, ArrayList<Robot.ArmNode>> neighbors;
    ArrayList<Robot.ArmNode> shortestPath;
    int height = 450;
    int width = 450;
    int k;

    // have this take in an array list of shapes from Robot
    public Draw(ArrayList<Line2D> links, ArrayList<Rectangle> obs, ArrayList<Robot.ArmNode> points,
                HashMap<Robot.ArmNode, ArrayList<Robot.ArmNode>> neighbors, int k, ArrayList<Robot.ArmNode> shortestPath){
        this.k = k;
        this.links = links;
        alpha = points;
        obstacles = obs;
        this.neighbors = neighbors;
        this.shortestPath = shortestPath;
        JPanel panel= new JPanel();
        getContentPane().add(panel);
        setSize(height,width);
        setVisible(true);
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        Color darkGreen = new Color(0,100,0);
        Color goodYell = new Color(229,229,18);

        g2.translate(height/2, width/2); // center origin in middle of panel
        g2.setColor(Color.BLUE);
        g2.scale(1,-1);


        for(int i = 0; i < links.size(); i++){
            g2.draw(links.get(i));
        }

        g2.setColor(Color.BLACK);
        for(int j = 0; j < obstacles.size(); j++){
            g2.draw(obstacles.get(j));
        }

//        g2.setColor(Color.RED);
//        for(int k = 0; k < alpha.size(); k++){
//            g2.drawRect((int)alpha.get(k).point.getX(), (int)alpha.get(k).point.getY(), 1, 1);
//        }

//        for(int h = 0; h < neighbors.keySet().size(); h++){
//            Robot.ArmNode currPoint = alpha.get(h);
//            ArrayList<Robot.ArmNode> pq = neighbors.get(currPoint);
//            ArrayList<Robot.ArmNode> pqTemp = pq;
//            for(int e = 0; e < k; e++){
//                if(pq.get(e) != null) {
//                    Robot.ArmNode currNeighbor = pq.get(e);
//                    g2.drawLine((int) currPoint.point.getX(), (int) currPoint.point.getY(), (int) currNeighbor.point.getX(), (int) currNeighbor.point.getY());
//                }
//            }
//            neighbors.put(currPoint, pqTemp);
//        }

        g2.setColor(Color.GREEN);

        // draw shortest path
        if(shortestPath != null) {
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                if (shortestPath.get(i + 1) != null) {
                    g2.drawLine((int) shortestPath.get(i).point.getX(), (int) shortestPath.get(i).point.getY(),
                            (int) shortestPath.get(i + 1).point.getX(), (int) shortestPath.get(i + 1).point.getY());
                    if(i%2 == 0){
                        if(i == 0)
                            g2.setColor(Color.RED);
                        else
                            g2.setColor(Color.BLUE);
                        for(int k = 0; k < links.size(); k++){
                            g2.draw(shortestPath.get(i).botLinks.get(k));
                        }
                        g2.setColor(Color.GREEN);
                    }
                }
            }
            g2.setColor(darkGreen);
            for(int k = 0; k < links.size(); k++){
                g2.draw(shortestPath.get(shortestPath.size()-1).botLinks.get(k));
            }
        }
    }
}