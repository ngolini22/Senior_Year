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
 *
 */

/*
 * Takes in a list of endpoints and draws lines between them.
 *
 * Endpoints calculated by endpointsCalculation in RobotCar class -> takes in angles and creates endpoints of each RobotCar
 *      link
 *
 */


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawCar extends JFrame{
    HashMap<RobotCar.CarNode, ArrayList<RobotCar.CarNode>> tree;
    ArrayList<RobotCar.CarNode> path;
    ArrayList<Line2D> walls;
    int height = 700;
    int width = 700;

    // have this take in an array list of shapes from RobotCar
    public DrawCar(HashMap<RobotCar.CarNode, ArrayList<RobotCar.CarNode>> tree, ArrayList<Line2D> walls, ArrayList<RobotCar.CarNode> path){
        this.walls = walls;
        this.tree = tree;
        this.path = path;
        JPanel panel= new JPanel();
        getContentPane().add(panel);
        setSize(width,height);
        setVisible(true);
    }

    public void paint(Graphics g){

        Graphics2D g2 = (Graphics2D) g;
        Color darkGreen = new Color(0,100,0);

        g2.translate(height/2, width/2); // center origin in middle of panel
        g2.scale(1,-1);

        // start node
        g2.drawRect(-1*(width/2) + 50, -1*(height/2) + 50, 10, 10);
        g2.drawRect((width/2) - 50, (height/2) - 70, 10, 10);

        g2.setColor(Color.RED);
        for(int i = 0; i < walls.size(); i++){
            g2.draw(walls.get(i));
        }

        g2.setColor(Color.BLACK);

        for(RobotCar.CarNode curr : tree.keySet()){
            ArrayList<RobotCar.CarNode> currNeighbors = tree.get(curr);
            for(int j = 0; j < currNeighbors.size();j++){
                RobotCar.CarNode neighbor = currNeighbors.get(j);
                g2.draw(new Line2D.Double(neighbor.x, neighbor.y, curr.x, curr.y));
            }
        }

        g2.setColor(Color.YELLOW);
        for(int i = 0; i < path.size()-2; i++){
            g2.drawLine((int)path.get(i).x, (int)path.get(i).y, (int)path.get(i+1).x, (int)path.get(i+1).y);
        }
    }
}