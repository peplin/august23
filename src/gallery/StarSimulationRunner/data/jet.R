#generate jet pulsar
setwd("/Users/dcguest/Desktop/points3d/")
PI=3.14159265
x=rnorm(100000)*30
r=sqrt(abs(x))
theta=runif(100000)*2*PI
y=r*sin(theta)
z=r*cos(theta)

data=cbind(x,y,z)
write.table(data,file="jet.csv",col.names=F,row.names=F,sep=",")

