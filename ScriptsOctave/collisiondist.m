data = load('../collisionIntervals50');
count = numel(data);  
dataMean = mean(data);
intervals = 2*max(data)/dataMean;
#h = hist(data2(:,1),intervals);%,"facecolor", "m"
H = hist(data(:,1), 41);%,"facecolor", "m"
xRange = 0:0.0025:0.1; 
bar(xRange, H./numel(data));  
#title ("Promedio de tiempo entre colisiones - N 150, T 300, S 5", 'fontsize',18);
xlabel ("Tiempo entre colisiones(s)", 'fontsize',16);
ylabel ("Probabilidad", 'fontsize',16);
set(gca,'fontsize',18);
