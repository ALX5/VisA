import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.frame.*;
import ij.process.ImageProcessor.*;
import ij.plugin.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.lang.Math.*;
import java.lang.Object.*;
import java.lang.String.*;
import java.awt.TextArea;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Window.*;


public class HCM_Visa_ implements PlugIn 
{

	class Vec 
	{
		int[] data = new int[3];	//*pointeur sur les composantes*/
	} 

	////////////////////////////////////////////////////
	Random r = new Random();    
	public int rand(int min, int max) 
	{
		return min + (int)(r.nextDouble()*(max-min));
	}


	////////////////////////////////////////////////////////////////////////////////////////////
	public void run(String arg) 
	{

		// LES PARAMETRES


		ImageProcessor ip;
		ImageProcessor ipseg;
		ImageProcessor ipJ;  
		ImagePlus imp;
		ImagePlus impseg;
		ImagePlus impJ;
		IJ.showMessage("Algorithme HCM","If ready, Press OK");
		ImagePlus cw;

		imp = WindowManager.getCurrentImage();
		ip = imp.getProcessor();

		int width = ip.getWidth();
		int height = ip.getHeight();


		impseg=NewImage.createImage("Image segmentée par HCM",width,height,1,24,0);
		ipseg = impseg.getProcessor();
		impseg.show();


		int nbclasses,nbpixels,iter;
		double stab,seuil,valeur_seuil;
		int i,j,k,l,imax,jmax,kmax;

		String demande =JOptionPane.showInputDialog("Nombre de classes : ");
		nbclasses =Integer.parseInt(demande);
		nbpixels = width * height; // taille de l'image en pixels

		double m = 1;

		demande =JOptionPane.showInputDialog("Nombre itération max : ");
		int itermax =Integer.parseInt(demande);

		demande =JOptionPane.showInputDialog("Valeur du seuil de stabilité : ");
		valeur_seuil =Double.parseDouble(demande);

		demande =JOptionPane.showInputDialog("Randomisation améliorée ? ");
		int valeur=Integer.parseInt(demande);


		double c[][] = new double[nbclasses][3]; 
		double cprev[][] = new double[nbclasses][3]; 
		int cidx[] = new int[nbclasses];
		//double m;
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		double red[] = new double[nbpixels];
		double green[] = new double[nbpixels];
		double blue[] = new double[nbpixels];
		int[] colorarray = new int[3];
		int[] init=new int[3];
		double figJ[]=new double[itermax];
		for(i=0;i<itermax;i++)
		{
			figJ[i]=0;
		}

		// Récupération des données images 
		l = 0;
		for(i = 0; i < width; i++)
		{
			for(j = 0; j < height; j++)
			{
				ip.getPixel(i,j,colorarray);
				red[l] = (double)colorarray[0];
				green[l] =(double) colorarray[1];
				blue[l] = (double)colorarray[2];
				l++;
			}
		}
		////////////////////////////////
		// HCM
		///////////////////////////////

		imax = nbpixels;  // nombre de pixels dans l'image
		jmax = 3;  // nombre de composantes couleur
		kmax=nbclasses;
		double data[][] = new double[nbclasses][3];
		int[] fixe=new int[3]; 
		int xmin = 0;
		int xmax = width;
		int ymin = 0;
		int ymax = height;
		int rx, ry;	
		int x,y;
		int epsilonx,epsilony;


		// Initialisation des centroïdes (aléatoirement )

		for(i=0;i<nbclasses;i++)
		{
			if(valeur>0) 
			{  
				epsilonx=rand((int)(width/(i+2)),(int)(width/2));
				epsilony=rand((int)(height/(4)),(int)(height/2));
				rx = rand(xmin+epsilonx, xmax-epsilonx);
				ry = rand(ymin+epsilony, ymax-epsilony);
				ip.getPixel(rx,ry,init);
			}
			else
			{
				switch(i)
				{
					case 0: ip.getPixel(50,50,init);break;
					case 1: ip.getPixel(150,50,init);break;
					case 2: ip.getPixel(250,50,init);break;
					case 3: ip.getPixel(50,250,init);break;
					case 4: ip.getPixel(150,250,init);break;
					default: ip.getPixel(250,250,init);break;
				}
			}

			c[i][0] = init[0]; c[i][1] =init[1]; c[i][2] = init[2];
		}
		
		// Calcul de distance entre data et centroides
		for(l = 0; l < nbpixels; l++)
		{
			for(k = 0; k < kmax; k++)
			{
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				Dprev[k][l] = r2 + g2 + b2;
			}
		}

		// Initialisation des degrés d'appartenance
		for(k=0; k<nbpixels; k++) {
			double min = Double.MAX_VALUE;
			int indexmin = -1;
			for (i=0; i<kmax; i++) {
				Umat[i][k] = 0;
				if(Dprev[i][k]<min) {
					min=Dprev[i][k];
					indexmin=i;
				}
			}
			Umat[indexmin][k]=1;
		}

		////////////////////////////////////////////////////////////
		// FIN INITIALISATION HCM
		///////////////////////////////////////////////////////////


		/////////////////////////////////////////////////////////////
		// BOUCLE PRINCIPALE
		////////////////////////////////////////////////////////////
		iter = 0;
		stab = 2;
		seuil = valeur_seuil;

		while ((iter < itermax) && (stab > seuil)) 
		{
			// Update  the matrix of centroids
			for(k=0; k<nbclasses; k++) {
				double sum=0;
				for(i =0; i<nbpixels; i++) {
					c[k][0]+=Math.pow(Umat[k][i],m)*red[i];
					c[k][1]+=Math.pow(Umat[k][i],m)*green[i];
					c[k][2]+=Math.pow(Umat[k][i],m)*blue[i];
					sum+=Math.pow(Umat[k][i], m);
				}
				if (sum!=0) {
					c[k][0]=c[k][0]/sum;
					c[k][1]=c[k][1]/sum;
					c[k][2]=c[k][2]/sum;
				} else {
					c[k][0]=0;
					c[k][1]=0;
					c[k][2]=0;
				}
			}

			// Compute Dmat, the matrix of distances (euclidian) with the centroïds
			for(l = 0; l < nbpixels; l++)
			{
				for(k = 0; k < nbclasses; k++)
				{
					double r2 = Math.pow(red[l] - c[k][0], 2);
					double g2 = Math.pow(green[l] - c[k][1], 2);
					double b2 = Math.pow(blue[l] - c[k][2], 2);
					Dmat[k][l] = r2 + g2 + b2;
				}
			}
			
			// Hard C-Means
			for(k=0; k<nbpixels; k++) {
				double min = Double.MAX_VALUE;
				int indexmin = -1;
				for (i=0; i<kmax; i++) {
					Umat[i][k] = 0;
					if(Dmat[i][k]<min) {
						min=Dmat[i][k];
						indexmin=i;
					}
				}
				Umat[indexmin][k]=1;
			}


			// Calculate difference between the previous partition and the new partition (performance index)
			double performanceIndex=0.0;
			for(i=0;i<nbclasses;i++)
				for(j =0;j<nbpixels;j++)
					performanceIndex += Math.pow(Umat[i][j], m)*Dmat[i][j]; 

			figJ[iter] = performanceIndex;

			iter++;
			////////////////////////////////////////////////////////

			Uprev=Umat;

			// Affichage de l'image segmentée 
			double[] mat_array=new double[nbclasses];
			l = 0;
			for(i=0;i<width;i++)
			{
				for(j = 0; j<height; j++)
				{
					for(k = 0; k<nbclasses; k++)
					{ 
						mat_array[k]=Umat[k][l];
					}
					int indice= IndiceMaxOfArray(mat_array,nbclasses) ;
					int array[] = new int[3];
					array[0] = (int)c[indice][0];
					array[1] = (int)c[indice][1];
					array[2] = (int)c[indice][2];
					ipseg.putPixel(i, j, array);
					l++;
				}
			}
			impseg.updateAndDraw();
			//////////////////////////////////
		}  // Fin boucle

		double[] xplot= new double[itermax];
		double[] yplot=new double[itermax];
		for(int w = 0; w < itermax; w++)
		{
			xplot[w]=(double)w;	yplot[w]=(double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (HCM)","iterations","J(P) value",xplot,yplot);
		plot.setLineWidth(2);
		plot.setColor(Color.blue);
		plot.show();
	} // Fin HCM
	int indice;
	double min,max;

	//Returns the maximum of the array

	public int  IndiceMaxOfArray(double[] array,int val) 
	{
		max=0;
		for (int i=0; i<val; i++)
		{
			if (array[i]>max) 
			{max=array[i];
			indice=i;
			}
		}
		return indice;
	}
}