data = load('../velocitythirds50.dat');#velocitythirds.dat  velocityt.dat
dataMean = mean(data);
H = hist(data(:,1), 41);%,"facecolor", "m"
xRange = 0:0.0025:0.1; 
bar(xRange, H./numel(data));
#hist(data(:,1), 50);
title ("PDF de velocidad  a 3/4 Sim. N 250, T 300", 'fontsize',18);
xlabel ("Velocidad (Ua/s)", 'fontsize',16);
ylabel ("Probabilidad", 'fontsize',16);
set(gca,'fontsize',18);
