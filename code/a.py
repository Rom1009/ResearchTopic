# def remove_commas_and_export(file_path, export_file_path):
#     try:
#         # Đọc nội dung từ tệp văn bản
#         with open(file_path, 'r') as file:
#             content = file.read()
        
#         # Xóa dấu phẩy từ nội dung
#         content_without_commas = content.replace(',', ' ')
        
#         # Ghi nội dung đã chỉnh sửa vào tệp mới
#         with open(export_file_path, 'w') as export_file:
#             export_file.write(content_without_commas)
        
#         print("Đã xóa dấu phẩy thành công và xuất ra tệp mới:", export_file_path)
    
#     except FileNotFoundError:
#         print("Không tìm thấy tệp", file_path)

# # Sử dụng hàm
# input_file_path = 'T40I10D100K_modified.txt'
# output_file_path = 'T40I10D100K_modified_1.txt'
# remove_commas_and_export(input_file_path, output_file_path)

import math

# Given values
epsilon = 3.8
eta = 8
tau = 0.1
sigma_X = 5

# Applying the given formula
term1 = (2 * math.sqrt(-2 * epsilon * math.log(1 - tau)) - math.log(tau) +
         math.sqrt(math.log(tau)**2 - 8 * epsilon * math.log(tau))) / (2 * eta)
term2 = (2 * epsilon - math.log(tau) +
         math.sqrt(math.log(tau)**2 - 8 * epsilon * math.log(tau))) / (2 * eta)
term3 = (sigma_X - epsilon + math.sqrt(eta - 2 * epsilon * math.log(1 - tau))) / eta
term4 = sigma_X / eta

xi = min(term1, term2, term3, term4)
print(term1+term2+term3+term4)