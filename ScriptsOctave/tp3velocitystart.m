data = load('../velocityfirst50.dat');#velocitythirds.dat  velocityt.dat
dataMean = mean(data);
h = hist(data(:,1), 15);%,"facecolor", "m"
xRange = 0:0.007:0.1; 
bar(xRange, h./numel(data));
title ("PDF de velocidad inicial - N 50", 'fontsize',18);
xlabel ("Velocidad (Ua/s)", 'fontsize',16);
ylabel ("Probabilidad", 'fontsize',16);
set(gca,'fontsize',18);
