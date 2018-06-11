def parse_data(file_name: str):
	ts_list, tj_list = [], []
	with open(file_name, "r") as file:
		data = file.read().splitlines()
		for line in data:
			ts, tj = line.split()
			ts_list.append(int(ts))
			tj_list.append(int(tj))
		print("-----------------------------------------")
		print(file_name)
		print(sum(ts_list)/len(ts_list)/(10**6), sum(tj_list)/len(tj_list)/(10**6))
		print("-----------------------------------------")
			

if __name__ == "__main__":
	file_list = ["log11.txt","log12.txt","log13.txt","log14.txt","log15.txt","log21.txt","log22.txt","log23.txt","log24.txt"]
	for file in file_list:
		parse_data(file)