class AddDeviceToTokens < ActiveRecord::Migration[5.2]
  def change
    add_column :tokens, :device_id, :string
    add_column :tokens, :device_type, :string
    add_column :tokens, :device_name, :string
    add_column :tokens, :device_location, :string
  end
end
