def remove_commas_and_export(file_path, export_file_path):
    try:
        # Đọc nội dung từ tệp văn bản
        with open(file_path, 'r') as file:
            content = file.read()
        
        # Xóa dấu phẩy từ nội dung
        content_without_commas = content.replace(',', ' ')
        
        # Ghi nội dung đã chỉnh sửa vào tệp mới
        with open(export_file_path, 'w') as export_file:
            export_file.write(content_without_commas)
        
        print("Đã xóa dấu phẩy thành công và xuất ra tệp mới:", export_file_path)
    
    except FileNotFoundError:
        print("Không tìm thấy tệp", file_path)

# Sử dụng hàm
input_file_path = 'T40I10D100K_modified.txt'
output_file_path = 'T40I10D100K_modified_1.txt'
remove_commas_and_export(input_file_path, output_file_path)

