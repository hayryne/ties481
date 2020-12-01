t<-read.table("E:\\kurssit\\simulation\\experiment_result_table.txt", header=TRUE)
summary(t)

model<- lm(queue ~ int_time + prep_time + rec_time + prep_units + rec_units + complications, data=t)
summary(model)