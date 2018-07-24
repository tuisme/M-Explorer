module Api
  class Api < Grape::API
    format :json
    prefix :api
    version 'v2'
    rescue_from :all do |e|
      error!({
        time: Time.now.to_s,
        status: "error",
        messages: e,
        user: {},
        clouds:{}
      }, 500)
    end

    helpers do
      def authorize_user!
        error!("Token Invalid", 400) unless headers["Access-Token"].present?
        error!("Unauthorize", 401) unless current_token
      end

      def current_token
        @current_token ||= Token.find_by_value(headers["Access-Token"])
      end

      def current_user
        @current_user ||= User.find_by_id(current_token.user_id)
      end
    end
    #mount V1::UserApi
    mount V2::UserApi
    mount V2::CloudApi
    mount V2::FolderApi
    mount V2::FileApi
    mount V2::ActionApi
    add_swagger_documentation
  end
end
