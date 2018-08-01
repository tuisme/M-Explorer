class AddOmniauthToUsers < ActiveRecord::Migration[5.2]
  def change
    add_column :users, :provider, :string
    add_column :users, :uid, :string
    add_column :users, :vip, :string
    add_column :users, :used, :string
    add_column :users, :allocated, :string
  end
end
