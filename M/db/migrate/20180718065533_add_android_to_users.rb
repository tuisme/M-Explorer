class AddAndroidToUsers < ActiveRecord::Migration[5.2]
  def change
    add_column :tokens, :android_id, :string
    add_column :tokens, :android_name, :string
  end
end
