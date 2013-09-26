/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.xhsi.flightdeck;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import net.sourceforge.xhsi.flightdeck.uh60m.UH60MGraphicsConfig;

/**
 *
 * @author annerajb
 */
public class DrawUtils 
{
    public static float Lerp(float start, float stop, float amount)
    {
        return start + (stop-start)*amount;
    }
    public static int round_to_ten(float number)
    {
        return Math.round(number / 10) * 10;
    }
    public static void drawStabilitator(Graphics2D g2,UH60MGraphicsConfig nd_gc,int x, int y, float degree)
    {
        Color temp = g2.getColor();
        Font fn = g2.getFont();
        if(degree == 0.0f)
        {
            g2.setColor(Color.RED);
            g2.fillRect(x, y, 40, 20);
            g2.setColor(Color.black);
            g2.setFont(nd_gc.font_m);
            g2.drawString("STB", x, y+15);
        }
        g2.setFont(fn);
        g2.setColor(temp);
    }
    public static void drawUHCompassRose(Graphics2D g2 , UH60MGraphicsConfig nd_gc,int x, int y, int diameter ,float map_up)
    {
        int big_tick_length = (int) (15 * nd_gc.shrink_scaling_factor);
        int small_tick_length = big_tick_length / 3;

        int two_digit_hdg_text_width = (int) nd_gc.get_text_width(g2, nd_gc.font_medium, "33");
        int one_digit_hdg_text_width = (int) nd_gc.get_text_width(g2, nd_gc.font_medium, "8");
        int hdg_text_height = (int) (nd_gc.get_text_height(g2, nd_gc.font_medium)*0.8f);
        // Compass rose for all modes except PLAN
        int half_view_angle = 180;
        float aircraft_track = map_up;
        int min_visible_heading = round_to_ten(aircraft_track - half_view_angle);
        int max_visible_heading = round_to_ten(aircraft_track + half_view_angle) + 5;
        int rose_y_offset = diameter + 4 + nd_gc.border_top;

        double rotation_offset = (-1 * half_view_angle)  + (min_visible_heading - (map_up - half_view_angle));

        AffineTransform original_at = g2.getTransform();

        // rotate according to horziontal path
        AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(Math.toRadians(rotation_offset),x,y);
        g2.transform(rotate_to_heading);

        Graphics g = (Graphics) g2;
        int tick_length = 0;
        g2.setFont(nd_gc.font_medium);
        for (int angle = min_visible_heading; angle <= max_visible_heading; angle += 5) 
        {
            //tick drawing code
            if (angle % 10 == 0) 
            {
                tick_length = big_tick_length;
            } else
            {
                tick_length = small_tick_length;
            }
            g.drawLine(x, rose_y_offset + 1,x, rose_y_offset + tick_length);
            //number 
            if (angle % 30 == 0) 
            {
                String text = "";
                if (angle < 0)
                {
                    text = "" + (angle + 360)/10;
                } else if (angle >=360) 
                {
                    text = "" + (angle - 360)/10;
                }else 
                {
                    text = "" + angle/10;
                }
                if(angle == 0) 
                {
                    text = "N";
                }else if(angle == 90) 
                {
                    text = "E";
                }else if(angle == 180 || angle == -180) 
                {
                    text = "S";
                }else if(angle == 270 || angle == -90 ) 
                {
                    text = "W";
                }
                int text_width;
                if (text.length() == 1)
                    text_width = one_digit_hdg_text_width;
                else
                    text_width = two_digit_hdg_text_width;

                g.drawString(text,x - (text_width/2),rose_y_offset + tick_length + hdg_text_height);
            }

            AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(5.0),x,y);
            g2.transform(rotate);

        }
        g2.setTransform(original_at);

        /*if (! nd_gc.mode_centered )
        {
            // 45 degrees marks for APP CTR, VOR CTR and MAP CTR
            int mark_length = nd_gc.big_tick_length;
            g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius - mark_length, nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius);
            g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius + mark_length);
            g2.drawLine(nd_gc.map_center_x - nd_gc.rose_radius - mark_length, nd_gc.map_center_y, nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y);
            g2.drawLine(nd_gc.map_center_x + nd_gc.rose_radius, nd_gc.map_center_y, nd_gc.map_center_x + nd_gc.rose_radius + mark_length, nd_gc.map_center_y);
            g2.transform(AffineTransform.getRotateInstance(Math.toRadians(45.0), nd_gc.map_center_x, nd_gc.map_center_y));
            g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius - mark_length, nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius);
            g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius + mark_length);
            g2.drawLine(nd_gc.map_center_x - nd_gc.rose_radius - mark_length, nd_gc.map_center_y, nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y);
            g2.drawLine(nd_gc.map_center_x + nd_gc.rose_radius, nd_gc.map_center_y, nd_gc.map_center_x + nd_gc.rose_radius + mark_length, nd_gc.map_center_y);
            g2.setTransform(original_at);
        }*/
    }
    public static void drawFuelGraph(Graphics2D g2, GraphicsConfig pfd_gc,int x, int y, float value)
    {
        int line_x_start = x+8;
        Color temp = g2.getColor();
        g2.setColor(Color.GREEN);
        int level;
        level = Math.round(Lerp(y+90,y,value));
        int graph_bottom = (y+90)-level;
        //level is how high the bar is
        //use level as y startpoint.
        //find how much till we reach the bottom
        g2.fillRect(line_x_start+4, level, 3, graph_bottom);
        g2.fillRect(line_x_start+10, level, 3, graph_bottom);
        //draw left triangle
        int triangle_x[] = {line_x_start,x+12,x+12};
        int triangle_y[] = {level,level,level+7};
        g2.fillPolygon(triangle_x, triangle_y, 3);
        //draw right triangle
        int right_triangle_x[] = {line_x_start+10,line_x_start+10+8,line_x_start+10};
        int right_triangle_y[] = {level,level,level+7};
        g2.fillPolygon(right_triangle_x, right_triangle_y, 3);
        g2.setColor(Color.white);
        //top line
        g2.drawLine(line_x_start, y, x+24, y);
        //mid line
        g2.drawLine(line_x_start, y+45, x+24, y+45);

        //bottom line
        g2.drawLine(x, y+90, x+30, y+90);
        
        g2.setColor(Color.YELLOW);
        //RTB line?
        g2.drawLine(line_x_start, y+75, x+24, y+75);
        //graph bars
        g2.setColor(temp);
    }
    //missing way to specify border width from text (when it get's converted into a class :D
    //this is broken it's bottom left instead of top left.
    public static void drawBoxedText(Graphics2D g2,GraphicsConfig pfd_gc,String txt, int x, int y,int line_height,int digit_height,Font font)
    {
        int line_width = pfd_gc.get_text_width(g2,font,txt) ;
        int border = 2;
        //this should be closing the rect
        g2.drawRect(x, y-2, line_width, line_height+2);
        //g2.clearRect(x - 34, y, 68, (y+line_height) - x); I think this is for being on top of another element
        g2.drawString(txt , x +border, (y+line_height) - border);
            
    }
    public static void drawAltReadOut(Graphics2D g2, GraphicsConfig pfd_gc,int value, int x,int y,boolean box)
    {
        int alt_int = (int)value;
        int alt_20 = alt_int / 20 * 20;
        float alt_frac = (value - (float)alt_20) / 20.0f;
        int alt_100 = (alt_int / 100) % 10;
        int alt_1k = (alt_int / 1000) % 10;
        int alt_10k = (alt_int / 10000) % 10;

        int x10k = x;
        int x1k = x10k + pfd_gc.digit_width_xxl;
        int x100 = x1k + pfd_gc.digit_width_xxl;
        int x20;
        if (value >= 0.0f)
        {
            x20 = x100 + pfd_gc.digit_width_l;
        }else
        {
            x20 = x100 + pfd_gc.digit_width_xl;
        }
         
        int ydelta = Math.round( pfd_gc.line_height_l*alt_frac );

        DecimalFormat decaform = new DecimalFormat("00");
        g2.setFont(pfd_gc.font_l);
        g2.drawString(decaform.format( (alt_20 + 40) % 100 ), x20, y + pfd_gc.line_height_l/2 - 2 + ydelta - pfd_gc.line_height_l*2);
        g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, y + pfd_gc.line_height_l/2 - 2 + ydelta - pfd_gc.line_height_l);
        g2.drawString(decaform.format( alt_20 % 100 ), x20, y + pfd_gc.line_height_l/2 - 2 + ydelta);
        if (alt_20 == 0) {
            g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, y + pfd_gc.line_height_l/2 - 2 + ydelta + pfd_gc.line_height_l);
        } else {
            g2.drawString(decaform.format( (alt_20 - 20) % 100 ), x20, y + pfd_gc.line_height_l/2 - 2 + ydelta + pfd_gc.line_height_l);
        }

        alt_20 %= 100;
        int line_height;
        int alt_100_y;
        int alt_100_y_1;
        int alt_1000_y;
        int alt_1000_y_1;
        int alt_10k_y;
        int alt_10k_y_1;
        // hundreds
         if (value >= 0.0f)
        {
            g2.setFont(pfd_gc.font_l);
            line_height = pfd_gc.line_height_l;
            alt_100_y = y + line_height/2 - 3 + ydelta;
            alt_100_y_1 = y + line_height/2 - 3 + ydelta - line_height;
            ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
            alt_1000_y = y + pfd_gc.line_height_xxl/2 - 4 + ydelta;
            alt_1000_y_1 = y + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl;
            alt_10k_y = y + pfd_gc.line_height_xxl/2 - 4 + ydelta;
            alt_10k_y_1 =y + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl;
        }else
        {
            g2.setFont(pfd_gc.font_xl);
            line_height = pfd_gc.line_height_xl;
            alt_100_y = y + line_height/2 - 3 - ydelta;
            alt_100_y_1 =  y + line_height/2 - 3 - ydelta + line_height;
            alt_1000_y = y + pfd_gc.line_height_xxl/2 - 4 - ydelta;
            alt_1000_y_1 = y + pfd_gc.line_height_xxl/2 - 4 - ydelta + pfd_gc.line_height_xxl;
            alt_10k_y =  y + pfd_gc.line_height_xxl/2 - 4 - ydelta;
            alt_10k_y_1 = y + pfd_gc.line_height_xxl/2 - 4 - ydelta + pfd_gc.line_height_xxl;
        }
        if ( alt_20 == 80 ) 
        {
            ydelta = Math.round( line_height*alt_frac );
            g2.drawString("" + alt_100, x100,alt_100_y );
            g2.drawString("" + (alt_100 + 1) % 10, x100,alt_100_y_1);
        } else {
            g2.drawString("" + alt_100, x100, y + line_height/2 - 3);
        }

        // thousands
        g2.setFont(pfd_gc.font_xxl);
        if ( ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
            g2.drawString("" + alt_1k, x1k, alt_1000_y);
            g2.drawString("" + (alt_1k + 1) % 10, x1k, alt_1000_y_1);
        } else {
            g2.drawString("" + alt_1k, x1k, y + pfd_gc.line_height_xxl/2 - 4);
        }

        // ten-thousands
        if ( ( alt_1k == 9 ) && ( alt_100 == 9 ) && ( alt_20 == 80 ) ) 
        {
            if ( alt_10k == 0) 
            {
                if(value >= 0.0f )
                {
                    if(box)
                    {
                        g2.setColor(pfd_gc.heading_labels_color.darker());
                        g2.fillRoundRect(x10k + pfd_gc.digit_width_xxl/8, y + pfd_gc.line_height_xxl/2 - pfd_gc.line_height_xxl*3/4 - 4 + ydelta, pfd_gc.digit_width_xxl*3/4, pfd_gc.line_height_xxl*3/4, (int)(8.0f*pfd_gc.scaling_factor), (int)(8.0f*pfd_gc.scaling_factor));
                        g2.setColor(pfd_gc.markings_color);
                    }
                }else
                {
                     g2.drawString("\u25CF", x10k, y + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                }
            } else {
                    g2.drawString("" + alt_10k, x10k,alt_10k_y);
            }
            g2.drawString("" + (alt_10k + 1) % 10, x10k, alt_10k_y_1);
        } else 
        {
            if ( alt_10k == 0) 
            {
                if(value >= 0.0f)
                {
                    if(box)
                    {
                        g2.setColor(pfd_gc.heading_labels_color.darker());
                        g2.fillRoundRect(x10k + pfd_gc.digit_width_xxl/8, y + pfd_gc.line_height_xxl/2 - pfd_gc.line_height_xxl*3/4 - 4, pfd_gc.digit_width_xxl*3/4, pfd_gc.line_height_xxl*3/4, (int)(8.0f*pfd_gc.scaling_factor), (int)(8.0f*pfd_gc.scaling_factor));
                        g2.setColor(pfd_gc.markings_color);
                    }
                }else
                {
                    g2.drawString("-", x10k, y + pfd_gc.line_height_xxl/2 - 4);
                }
            } else 
            {
                g2.drawString("" + alt_10k, x10k, y + pfd_gc.line_height_xxl/2 - 4);
            }
        }
    }
    public static void drawArrow(Graphics2D g2,int x, int y,int lenght, double angle)
    {
        AffineTransform original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(angle),x,y);
        g2.transform(rotate);

        GeneralPath polyline;

        polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 2);
        polyline.moveTo (x, y - (lenght/2));
        polyline.lineTo(x, y + (lenght/2));
        polyline.lineTo(x + 5, y + (lenght/2) - 5);
        g2.draw(polyline);
        polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 2);
        polyline.moveTo (x, y - (lenght/2));
        polyline.lineTo(x, y + (lenght/2));
        polyline.lineTo(x - 5, y + (lenght/2) - 5);
        g2.draw(polyline);
        g2.setTransform(original_at);
    }
    public static void drawCompassAircraft(Graphics2D g2, int x, int y)
    {
        //aircraft body
        g2.drawLine(x, y-4, x, y+15);
        //aicraft wings
        g2.drawLine(x+10, y, x-10, y);
        //aircraft tail wing
        g2.drawLine(x+3, y+12, x-3, y+12);
    }
}
