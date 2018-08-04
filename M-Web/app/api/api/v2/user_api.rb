module Api::V2
  class UserApi < Grape::API
    namespace :users do

      params do
        requires :provider, type: String
        requires :email, type: String
        requires :first_name, type: String
        requires :last_name, type: String
        requires :device_id, type: String
        requires :device_type, type: String
        requires :device_name, type: String
        requires :device_location, type: String
      end
      post :provider do
        return_user = OpenStruct.new
        default_avatar = 'https://mexplorer.herokuapp.com/default-avatar.png'
        if user = User.find_by(email: params[:email])
          return_user.token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: return_user.token, device_id: params[:device_id], device_type: params[:device_type],
                             device_name: params[:device_name], device_location: params[:device_location])
          return_user.email = user.email
          return_user.first_name = user.first_name
          return_user.last_name = user.last_name
          return_user.avatar_url = user.avatar_url
          return_user.used = user.used
          return_user.allocated = user.allocated
          return_user.is_vip = user.vip

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Login Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        else
          password = SecureRandom.urlsafe_base64(nil, true)
          user = User.create(provider: params[:provider], email: params[:email], password: password, uid: params[:uid], first_name: params[:first_name],
                             last_name: params[:last_name], avatar_url: default_avatar, used: 0, allocated: 10_737_418_240, vip: 'false')

          return_user.email = user.email
          return_user.first_name = user.first_name
          return_user.last_name = user.last_name
          return_user.avatar_url = user.avatar_url
          return_user.used = user.used
          return_user.allocated = user.allocated
          return_user.is_vip = user.vip
          return_user.token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: return_user.token, device_id: params[:device_id], device_name: params[:device_name])
          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Login Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        end
      end

      params do
        requires :email, type: String
        requires :password, type: String
        requires :device_id, type: String
        requires :device_type, type: String
        requires :device_name, type: String
        requires :device_location, type: String
      end
      post :signin do
        return_user = OpenStruct.new
        user = User.find_by(email: params[:email])
        raise ActiveRecord::RecordNotFound.new('User not found!') unless user
        if user.valid_password?(params[:password])
          return_user.token = loop do
            random_token = SecureRandom.urlsafe_base64(nil, true)
            break random_token unless Token.exists?(value: random_token)
          end
          user.tokens.create(value: return_user.token, device_id: params[:device_id], device_type: params[:device_type],
                             device_name: params[:device_name], device_location: params[:device_location])
          return_user.email = user.email
          return_user.first_name = user.first_name
          return_user.last_name = user.last_name
          return_user.avatar_url = user.avatar_url
          return_user.used = user.used
          return_user.allocated = user.allocated
          return_user.is_vip = user.vip

          present :time, Time.now.to_s
          present :status, 'success'
          present :message, 'Login Successfully!'
          present :data, return_user, with: Api::Entities::UserEntity
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'Invalid email or password!',
            data: nil
          }
        end
      end

      params do
        requires :email, type: String
        requires :password, type: String
        requires :first_name, type: String
        requires :last_name, type: String
      end
      post :signup do
        default_avatar = 'https://mexplorer.herokuapp.com/default-avatar.png'
        if User.create!(email: params[:email], password: params[:password],
                        first_name: params[:first_name], last_name: params[:last_name], avatar_url: default_avatar, used: 0, allocated: 10_737_418_240, vip: 'false')
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Registration successful. You can log in now!',
            data: nil
          }
        end
      end

      params do
        requires :email, type: String
      end
      post :forget_password do
        if User.find_by_email(params[:email])
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'Reset Password email has been sent!',
            data: nil
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: "User doesn't exist!",
            data: nil
          }
        end
      end

      desc 'Logout a user.'
      post :logout do
        if current_token
          Token.destroy(current_token.id)
          {
            time: Time.now.to_s,
            status: 'success',
            message: 'You have successfully logged out!'
          }
        else
          {
            time: Time.now.to_s,
            status: 'error',
            message: 'You need to be logged in to log out!'
          }
        end
      end
    end
  end
end
