// 2. Opérateurs de la logique floue. 
    function C=operateurmin(A,B)
//        [rows,cols]=size(A);
//        for i=1:cols
//            if basse(i)>moyenne(i) then
//                C(i)=moyenne(i);
//            else
//                C(i)=basse(i); 
//            end
//        end
        C=min(A,B);
    endfunction
    
    function C=operateurmax(A,B)
//        [rows,cols]=size(A);
//        for i=1:cols
//            if basse(i)>moyenne(i) then
//                C(i)=basse(i);
//            else
//                C(i)=moyenne(i); 
//            end
//        end
        C=max(A,B);
    endfunction
    
    function A=appartenance(temperature,abcisses,sousensemble)
        i=1;
        while temperature>abcisses(1,i),
            i=i+1;
        end
        // Calcul du degré d'appartenance à l'ensemble basse.
        m=(sousensemble(i)-sousensemble(i-1))/(abcisses(i)-abcisses(i-1));
        p=sousensemble(i-1)-(m*abcisses(i-1));
        A=m*temperature+p;
    endfunction

// 1. Fonctions. 
    // GENERATION DES ENSEMBLES FLOUS
    abcisses=[0,5,10,15,20,25,30,35,40];
    
    // Ensembles flous.
    basse=[1,1,1,0.5,0,0,0,0,0];
    moyenne=[0,0,0,0.5,1,0.5,0,0,0];
    elevee=[0,0,0,0,0,0.5,1,1,1];
    
    subplot(221);
    plot2d(abcisses,basse,style=2);
    plot2d(abcisses,moyenne,style=14);
    plot2d(abcisses, elevee,style=5);
    xtitle("Partition floue de l univers du discours","Température (°C)","Degré d appartenance");
//    
    // DEGRES D'APPARTENANCE AUX DIFFERENTS SOUS-ENSEMBLES
    temperature = 16;
    degres_appartenance=[appartenance(temperature,abcisses,basse);appartenance(temperature,abcisses,moyenne);appartenance(temperature,abcisses,elevee)];
    
    // TRACAGE DU GRAPHIQUE DE L'ENSEMBLE FLOU 'TEMPERATURE BASSE OU MOYENNE'
//    for i=1:9
//        if basse(i)>moyenne(i) then
//            basse_ou_moyenne(i)=basse(i);
//        else
//            basse_ou_moyenne(i)=moyenne(i); 
//        end
//    end
    basse_ou_moyenne=operateurmax(basse,moyenne);
    subplot(222);
    plot2d(abcisses,basse_ou_moyenne);
    xtitle("Température basse ou moyenne","Température (°C)");
    
// 3. Implication floue.
    abcisses_mamdani=[0,5,8,10,15,20,25,30,35,40];
    chauffer_fort=[0,0,0,1,1,1,1,1,1,1];
    subplot(223);
    plot2d(abcisses_mamdani,chauffer_fort,style=21);
    xtitle("Chauffer fort","Puissance chauffe (KW)");
    temperature_mesuree=12;
    basse_mamdani=[1,1,1,1,0.5,0,0,0,0,0];
    degres_appartenance_mamdani=appartenance(temperature_mesuree,abcisses_mamdani,basse_mamdani);
    implication_mamdani=operateurmin(chauffer_fort,degres_appartenance_mamdani);
    subplot(224);
    plot2d(abcisses_mamdani,implication_mamdani,style=21);
    xtitle("Puissance de Chauffe","Puissance chauffe (KW)");
